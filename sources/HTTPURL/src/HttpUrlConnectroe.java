import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUrlConnectroe {
	
	public static void main(String[] args) throws IOException {
		try {
			URL url = new URL("http://localhost:8090");
			HttpURLConnection connector = (HttpURLConnection)url.openConnection();
			System.out.println(url.openConnection().getContentType());
			connector.setDoOutput(true);
			connector.setRequestMethod("POST");
			InputStream is = (InputStream)connector.getContent();
			byte[] data = new byte[is.available()];
			is.read(data);
			
			
			System.out.println(new String(data));
			OutputStream outputStream = connector.getOutputStream();
			outputStream.write(data);
			outputStream.flush();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
