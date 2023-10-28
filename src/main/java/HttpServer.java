import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.io.*;

public class HttpServer {
    private final int port;
    private static final String HTML_FILE = "src/main/resources/salary.html";

    public HttpServer(int port) {
        this.port = port;
    }

    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            var socket = serverSocket.accept();
            processSocket(socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processSocket(Socket socket) throws IOException {
        try (socket;
             var inputStream = new DataInputStream(socket.getInputStream());
             var outputStream = new DataOutputStream(socket.getOutputStream())) {
            //Обработка входящего запроса
            var bf = new BufferedReader(new InputStreamReader(inputStream));
            int bodyLength = 0;
            String line;
            StringBuilder header = new StringBuilder();
            while (!((line = bf.readLine()).isEmpty())) {
                if (line.startsWith("Content-Length")) {
                    String[] contentLenght = line.split("\\s");
                    bodyLength = Integer.parseInt(contentLenght[1]);
                }
                header.append(line);
            }
            byte[] bytes = inputStream.readNBytes(bodyLength);
            ObjectMapper mapper = new ObjectMapper();
            List<Employee> employees = mapper.readValue(bytes, SalaryInfo.class).getEmployees();
            System.out.println(employees);
            int salary = employees.stream().mapToInt(Employee::getSalary).sum();
            int tax = employees.stream().mapToInt(Employee::getTax).sum();

            //Ответ сервера на запрос
            byte[] body = Files.readAllBytes(Path.of("src/main/resources/salary.html"));
            body = new String(body)
                    .replace("${total_income}", String.valueOf(salary))
                    .replace("${total_tax}", String.valueOf(tax))
                    .replace("${total_profit}", String.valueOf(salary - tax))
                    .getBytes();

            outputStream.write("""
                    HTTP/1.1 200 OK
                    content-type:text/html
                    content-length: %s
                    """.formatted(body.length).getBytes());
            outputStream.write(System.lineSeparator().getBytes());
            outputStream.write(body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
