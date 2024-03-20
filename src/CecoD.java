import java.net.*; // Importa clases para manejo de sockets y direcciones IP.
import java.io.*; // Importa clases para entrada/salida.
import java.nio.ByteBuffer; // Importa ByteBuffer para manipulación de datos binarios.

public class CecoD {
    public static void main(String[] args) {
        try {
            int pto = 1234; // Puerto en el que el servidor escucha.
            String dir = "127.0.0.1"; // Dirección IP del servidor.
            InetAddress dst = InetAddress.getByName(dir); // Convierte la dirección en un objeto InetAddress.
            DatagramSocket cl = new DatagramSocket(); // Crea un socket UDP para el cliente.
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); // Preparación para leer entrada del usuario.
            int tam = 10; // Define el tamaño máximo de fragmento de mensaje.
            int numDatagrama = 0; // Contador para el número de datagrama.

            while (true) {
                System.out.println("Escribe un mensaje, <Enter> para enviar, \"salir\" para terminar");
                String msj = br.readLine(); // Lee mensaje de usuario.

                if (msj.compareToIgnoreCase("salir") == 0) { // Condición de salida.
                    br.close(); // Cierra el BufferedReader.
                    cl.close(); // Cierra el socket del cliente.
                    System.exit(0); // Finaliza el programa.
                } else {
                    byte[] b = msj.getBytes(); // Convierte el mensaje a bytes.
                    int longitudTotal = b.length; // Longitud total del mensaje.
                    int offset = 0; // Offset para manejar fragmentación.

                    // Loop para manejar fragmentación si es necesario.
                    while (offset < longitudTotal) {
                        numDatagrama++; // Incrementa el contador de datagramas por cada fragmento o mensaje.
                        int longitudFragmento = Math.min(tam, longitudTotal - offset); // Calcula longitud del fragmento.
                        ByteBuffer buffer = ByteBuffer.allocate(4 + longitudFragmento); // Buffer para número de datagrama + fragmento.
                        buffer.putInt(numDatagrama); // Inserta número de datagrama al inicio del buffer.
                        buffer.put(b, offset, longitudFragmento); // Añade fragmento de mensaje al buffer.

                        byte[] fragmentoConNum = buffer.array(); // Convierte el buffer a arreglo de bytes.
                        DatagramPacket p = new DatagramPacket(fragmentoConNum, fragmentoConNum.length, dst, pto); // Prepara datagrama.
                        cl.send(p); // Envía datagrama.

                        // Preparación para recibir el eco.
                        byte[] bufferEco = new byte[4 + tam]; // Buffer para recibir el eco (con espacio para número de datagrama).
                        DatagramPacket p1 = new DatagramPacket(bufferEco, bufferEco.length); // Prepara datagrama para eco.
                        cl.receive(p1); // Recibe eco.

                        // Procesa el eco recibido.
                        ByteBuffer bufferRecepcion = ByteBuffer.wrap(p1.getData()); // Envuelve datos recibidos.
                        int numEco = bufferRecepcion.getInt(); // Extrae número de datagrama del eco.
                        byte[] ecoBytes = new byte[p1.getLength() - 4]; // Extrae el mensaje del eco.
                        bufferRecepcion.get(ecoBytes, 0, p1.getLength() - 4); // Obtiene mensaje del eco.

                        System.out.println("Eco recibido del datagrama " + numEco + ": " + new String(ecoBytes)); // Imprime eco.

                        offset += longitudFragmento; // Actualiza offset para próximo fragmento.
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Maneja excepciones.
        }
    }
}
