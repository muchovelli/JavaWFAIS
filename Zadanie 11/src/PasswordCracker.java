import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PasswordCracker implements PasswordCrackerInterface {
    List<Character> passwordComponents;

    public String getPassword(String host, int port) {
        String tocrack = null;
        boolean ifPassed = false;
        BufferedReader reader = null;
        OutputStream out = null;
        PrintWriter writer = null;
        passwordComponents = new ArrayList<>();
        InputStreamReader input = null;
        InputStream in;
        int index = 0;
        int indexOfElements;
        int last_iteration = 0;
        String potentialBuilder;
        String s;
        String check;
        List<Integer> tab;
        try {
            Socket socket = new Socket(host, port);
            in = socket.getInputStream();
            input = new InputStreamReader(in);
            reader = new BufferedReader(input);
            out = socket.getOutputStream();
            writer = new PrintWriter(out, true);
            reader.readLine();
            writer.println("Program");
            reader.readLine();
            String password = reader.readLine().replaceAll("schema : ", "");
            reader.readLine();
            passwordComponents = PasswordComponents.decodePasswordSchema(password);
            int[] elements = new int[passwordComponents.size()];
            tab = passwordComponents.stream().map(character -> PasswordComponents.passwordComponents.get(character).size()).collect(Collectors.toList());
            do {
                potentialBuilder = IntStream.range(0, passwordComponents.size()).mapToObj(i -> String.valueOf(PasswordComponents.passwordComponents.get(passwordComponents.get(i)).get(elements[i]))).collect(Collectors.joining());
                s = potentialBuilder;
                writer.println(s);
                check = reader.readLine();
                if (check.equals("+OK")) {
                    tocrack = s;
                    if (!tocrack.equals("")) {
                        ifPassed = true;
                    }
                } else {
                    indexOfElements = elements[index];
                    check = check.replaceAll("[^\\d]", "");
                    if (last_iteration <= Integer.parseInt(check)) {
                        if (tab.get(index) != indexOfElements + 1) {
                            elements[index] = indexOfElements + 1;
                            last_iteration = Integer.parseInt(check);
                        } else index++;
                    } else {
                        elements[index] = indexOfElements - 1;
                        index++;
                    }
                }
            } while (!ifPassed);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (input != null) {
                    input.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tocrack;
    }
}