import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

import static java.net.http.HttpRequest.BodyPublishers.ofFile;

public class HttpClientRunner {
    private static final String HTML_FILE = "src/main/resources/salary.html";
    private static final String JSON_FILE = "src/main/resources/info.json";

    public static void main(String[] args) throws IOException, InterruptedException {
        var httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082"))
                .header("content-type", "application/json")
                .POST(ofFile(Path.of(JSON_FILE)))
                .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.headers());
        System.out.println(response.body());
        try (FileWriter fileWriter = new FileWriter(HTML_FILE)) {
            fileWriter.write(response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
