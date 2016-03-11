//package jsonIndexer;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Map;
//
//import org.codehaus.jackson.map.JsonMappingException;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jackson.type.TypeReference;
//
//import com.fasterxml.jackson.core.JsonGenerationException;
////import com.fasterxml.jackson.core.type.TypeReference;
//
//public class TestJacksonFileRead {
//
//	public static void main(String[] args) {
//
//		try {
//
//			ObjectMapper mapper = new ObjectMapper();
//
//			// read JSON from a file
//			Map<String, Object> map = mapper.readValue(
//					new File("k:\\MOCK_DATA.json"), 
//					new TypeReference<Map<String, Object>>() {
//			});
//
//			System.out.println(map.get("name"));
//			System.out.println(map.get("age"));
//
//			@SuppressWarnings("unchecked")
//			ArrayList<String> list = (ArrayList<String>) map.get("messages");
//
//			for (String msg : list) {
//				System.out.println(msg);
//			}
//
//		} catch (JsonGenerationException e) {
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//}