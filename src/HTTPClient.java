import java.io.*;
import java.net.*;

public class HTTPClient {

    public static void main(String[] args) {
        // URL que l'on veut récupérer avec l'hôte, le port et le chemin pour récupérer les fichiers HTML.

        String host = args[0];
        int port = 80;
        String chemin = "/";

        try {
            requeteHTTP(host, port, chemin);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void requeteHTTP(String host, int port, String chemin) throws IOException {
        // Création de la connexion TCP avec le socket
        Socket socket = new Socket(host, port);
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // On spéficie ici que l'on envoie une méthode GET au serveur Web
        out.println("GET " + chemin + " HTTP/1.1");
        out.println("Host: " + host);
        out.println("Connection: close");
        out.println();
        out.flush();

        // Dans cette boucle while, on ajoute toutes les lignes du site demandé puis on les stocke dans un StringBuild pour pouvoir l'afficher par la suite dans le terminal.
        String message;
        StringBuilder response = new StringBuilder();
        while ((message = in.readLine()) != null && !message.isEmpty()) {
            response.append(message).append("\n");
        }

        // Si le code de statut est 200, alors on continue le traitement
        if (response.toString().contains("HTTP/1.1 2")) {
            System.out.println(response.toString());

            // On extrait les en-têtes et les informations utiles
            String[] lignes = response.toString().split("\n");
            for (String ligne : lignes) {
                if (ligne.trim().isEmpty()) {
                    break;
                }
                if (ligne.startsWith("Cookie:") || ligne.startsWith("Set-Cookie:") || ligne.startsWith("OtherHeader:")) {
                    System.out.println(ligne);
                }
            }
        } else if (response.toString().contains("HTTP/1.1 401")) {
            System.out.println("Erreur 401 : Accès non autorisé.");
        } else if (response.toString().contains("HTTP/1.1 3")) {
            String newURL = extractURL(response.toString());
            System.out.println("Redirection vers : " + newURL);

            in.close();
            out.close();
            socket.close();

            URL redirectUrl = new URL(newURL);
            host = redirectUrl.getHost();
            port = redirectUrl.getPort() != -1 ? redirectUrl.getPort() : 80;
            chemin = redirectUrl.getPath();

            requeteHTTP(host, port, chemin);
        } else {
            System.out.println("Erreur : code de status HTTP non connu.");
        }

        in.close();
        out.close();
        socket.close();
    }

    public static String extractURL(String response) {
        int index = response.indexOf("Location:");
        if (index != -1) {
            int index2 = response.indexOf("\n", index);
            if (index2 != -1) {
                return response.substring(index + 10, index2).trim();
            }
        }
        return null;
    }
}

