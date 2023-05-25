import java.io.*;
import java.net.*;

public class HTTPServer {

    public static void main(String[] args) {
        // On définit le numéro de port sur lequel le serveur va écouter
        int port = 80;

        try {
            // On initialise un socket côté serveur afin qu'il écoute sur le port spécifié
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Serveur HTTP en écoute sur le port " + port + "...");

            while (true) {
                // On accepte la connexion du client sur le serveur
                Socket client = serverSocket.accept();
                // On affiche l'adresse IP du client connecté
                System.out.println("Connexion en cours : " + client.getInetAddress().getHostAddress());

                // On instancie un nouveau Thread pour qu'un client puisse se connecter, on appelle ensuite la classe RequeteClient
                Thread thread = new Thread(new RequeteClient(client));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class RequeteClient implements Runnable {
        private Socket client;

        public RequeteClient(Socket client) {
            this.client = client;
        }

        public void run() {
            try {
                // On initialise un BufferedReader pour lire les données contenues dans la requête.
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                StringBuilder request = new StringBuilder();
                String message;

                // On lit toutes les lignes de la requête HTTP puis on les met en forme pour la rendre lisible
                while ((message = in.readLine()) != null && !message.isEmpty()) {
                    request.append(message).append('\n');
                }

                System.out.println("Requête : " + request);

                // On récupère les informations de la requête HTTP : la méthode et l'uri pour identifier le fichier
                String[] requeteLigne = request.toString().split("\n");
                String[] requeteParts = requeteLigne[0].split(" ");
                String method = requeteParts[0];
                String uri = requeteParts[1];

                // Si la méthode est GET, alors on retourne la page demandé par l'utilisateur
                if (method.equals("GET")) {
                    File file = new File("." + uri);
                    // On vérifie que la page html existe
                    if (file.exists() && !file.isDirectory()) {
                        BufferedReader fileReader = new BufferedReader(new FileReader(file));

                        PrintWriter out = new PrintWriter(client.getOutputStream());
                        out.println("HTTP/1.1 200 OK");
                        out.println("Content-Type: text/html");
                        out.println();

                        // On lit toutes les lignes du fichier html, puis on les affiche
                        String line;
                        while ((line = fileReader.readLine()) != null) {
                            out.println(line);
                        }

                        out.flush();
                        fileReader.close();
                    } else {
                        // Si elle n'existe pas, on retourne un code et un message d'erreur
                        PrintWriter out = new PrintWriter(client.getOutputStream());
                        out.println("HTTP/1.1 404 Not Found");
                        out.println("Content-Type: text/plain");
                        out.println();
                        out.println("Fichier inexistant");
                        out.flush();
                    }
                }
                // Si la méthode est POST, on retourne la page demandé puis on actualise les informations
                else if (method.equals("POST")) {
                    File file = new File("." + uri);
                    // On vérifie que la page html existe
                    if (file.exists() && !file.isDirectory()) {
                        StringBuilder body = new StringBuilder();

                        String nameValue = "";
                        String[] values = body.toString().split("&");
                        for (String value : values) {
                            String[] index = value.split("=");
                            if (index.length == 2 && index[0].equals("truc")) {
                                nameValue = URLDecoder.decode(index[1], "UTF-8");
                                break;
                            }
                        }

                        BufferedReader fileReader = new BufferedReader(new FileReader(file));
                        StringBuilder fileContent = new StringBuilder();
                        String line;
                        while ((line = fileReader.readLine()) != null) {
                            if (line.contains("truc")) {
                                line = line.replace("truc", nameValue);
                            }
                            fileContent.append(line).append("\n");
                        }
                        fileReader.close();

                        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));
                        fileWriter.write(fileContent.toString());
                        fileWriter.flush();
                        fileWriter.close();

                        PrintWriter out = new PrintWriter(client.getOutputStream());
                        out.println("HTTP/1.1 200 OK");
                        out.println("Content-Type: text/plain");
                        out.println("Content-Length: " + body.length());
                        out.println();
                        out.flush();
                    } else {
                        // Si elle n'existe pas, on retourne un code et un message d'erreur
                        PrintWriter out = new PrintWriter(client.getOutputStream());
                        out.println("HTTP/1.1 404 Not Found");
                        out.println("Content-Type: text/plain");
                        out.println();
                        out.println("Fichier inexistant");
                        out.flush();
                    }
                } else {
                    // Si ce n'est pas une méthode GET ou POST, on retourne un message d'erreur
                    System.out.println("Méthode non supportée");
                }

                in.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
