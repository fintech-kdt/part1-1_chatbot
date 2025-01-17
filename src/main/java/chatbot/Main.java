package chatbot;

import java.util.concurrent.TimeUnit;

import chatbot.biz.ChatbotApiService;
import chatbot.model.UpdateResponse;
import chatbot.model.UpdateResponse.Update;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main {
	public static void main(String[] args) {
		// 텔레그램 봇의 토큰을 설정
		// https://web.telegram.org/a
		// https://telegram.me/BotFather
		final String TOKEN = ""; // 여기에 텔레그램 봇 토큰을 입력하세요

		// Retrofit 인스턴스를 생성하여 텔레그램 API와 통신 설정
		Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.telegram.org/bot" + TOKEN + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

		// ChatbotApiService 인터페이스의 구현체를 생성
        ChatbotApiService service = retrofit.create(ChatbotApiService.class);
		
    	try { 
    		// 처음으로 업데이트를 가져와서 마지막 업데이트 ID를 설정
    		UpdateResponse response = service.getUpdates(0).execute().body();
    		long lastId = 0;
    		if (response.result.size() != 0) {    			
    			lastId = response.result.get(response.result.size() - 1).updateId;
    		}

    		while (true) {
				// 1초마다 새로운 업데이트를 체크
				TimeUnit.SECONDS.sleep(1);
				
				// 새로운 업데이트를 가져옴
				response = service.getUpdates(lastId + 1).execute().body();
				for (Update update : response.result) {
					long id = update.message.from.id;
					String text = update.message.text;
					
					// 메시지의 사용자 ID와 내용을 출력
					System.out.println(id);
					System.out.println(text);
					
					// 사용자가 보낸 메시지에 답장 전송
					service.sendMessage(id + "", text + "(이)가 무슨 뜻이죠?").execute().body();
					
					// 마지막 업데이트 ID를 갱신
					lastId = update.updateId;
				}
    		}
		} catch (Exception e) {
			// 예외 발생 시 스택 트레이스를 출력
			e.printStackTrace();
		}
	}
}
