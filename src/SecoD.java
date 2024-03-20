import java.net.*; // Importa clases para manejo de sockets y direcciones IP.
import java.nio.ByteBuffer; // Importa ByteBuffer para manipulación de datos binarios.

public class SecoD {
    public static void main(String[] args){
        try {
            int pto = 1234; // Puerto en el que el servidor escucha.
            DatagramSocket s = new DatagramSocket(pto); // Crea un socket UDP para el servidor.
            System.out.println("Servidor iniciado... esperando datagramas.."); // Mensaje inicial.

            while(true) { // Bucle infinito para mantener el servidor corriendo y esperando datagramas.
                DatagramPacket p = new DatagramPacket(new byte[65535], 65535); // Crea un paquete para recibir datos.
                s.receive(p); // Espera y recibe un datagrama del cliente.

                ByteBuffer wrapped = ByteBuffer.wrap(p.getData(), 0, 4); // Envuelve los primeros 4 bytes del paquete en un ByteBuffer.
                int numeroDatagrama = wrapped.getInt(); // Extrae el número de secuencia del datagrama.

                // Imprime el número de datagrama y el mensaje recibido, omitiendo los primeros 4 bytes del número de secuencia.
                System.out.println("Datagrama número " + numeroDatagrama + " recibido desde " + p.getAddress() + ":" + p.getPort() + " con el mensaje: " + new String(p.getData(), 4, p.getLength() - 4));

                // Eco del mensaje incluyendo el número de datagrama.
                // Aquí simplemente reutiliza los datos recibidos en el paquete `p`, ya que incluyen tanto el número de secuencia como el mensaje.
                s.send(new DatagramPacket(p.getData(), p.getLength(), p.getAddress(), p.getPort()));
            }
        } catch(Exception e) {
            e.printStackTrace(); // Captura y muestra cualquier excepción.
        }
    }
}
