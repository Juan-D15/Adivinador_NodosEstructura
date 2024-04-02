package adivinador;

import java.io.*;
import javax.swing.*;
import java.io.Serializable;

class Arbol implements Serializable {

    //Estructura del Arbol
    private String dato;
    private Arbol izquierda;
    private Arbol derecha;

    //Constructor
    public Arbol(String dato) {
        this.dato = dato;
        this.izquierda = null;
        this.derecha = null;
    }

    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;
    }

    public Arbol getIzquierda() {
        return izquierda;
    }

    public void setIzquierda(Arbol izquierda) {
        this.izquierda = izquierda;
    }

    public Arbol getDerecha() {
        return derecha;
    }

    public void setDerecha(Arbol derecha) {
        this.derecha = derecha;
    }
}

public class Adivinador {
    //Ventanas emergentes
    public static String ventanaOpciones(String txt) {
        String[] opciones = {"Si", "No"};
        int opcionSeleccionada = JOptionPane.showOptionDialog(null, txt, "Adivinador", 0, JOptionPane.QUESTION_MESSAGE, null, opciones, "");
        //Opcion seleccionada (Si o No)
        return opciones[opcionSeleccionada];
    }
    //Ventana de dialogo
    public static String ventanaDialogo(String txt) {
        String dato = JOptionPane.showInputDialog(txt);
        return dato;
    }
    //Ventana de mensaje
    public static void ventanaMensaje(String txt) {
        JOptionPane.showMessageDialog(null, txt);
    }

    //Carga los nodos que se hayan creado si se quiere volver a ejecutar el programa
    public static Arbol cargarDatosArbol() {
        Arbol raiz = null;
        try {
            FileInputStream fileIn = new FileInputStream("baseDatos.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            raiz = (Arbol) in.readObject();
            in.close();
            fileIn.close();
        } catch (FileNotFoundException e) {
            //Crear archivo .ser
            raiz = null; // Crea un nuevo árbol
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return raiz;
    }

    //Guarda los nodos que se hayan creado
    public static void guardarDatosArbol(Arbol arbol) {
        try {
            FileOutputStream fileOut = new FileOutputStream("baseDatos.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(arbol);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Método para la respuesta SI
    public static boolean respuesta_Si(String texto) {
        //Pasa la respuesta a minusculas
        String resp = ventanaOpciones(texto).toLowerCase();
        //Toma la primera letra del texto colocado
        return resp.charAt(0) == 's';
    }

    //Crea la raiz del arbol
    public static Arbol raizArbol() {
        Arbol raiz = cargarDatosArbol();
        if (raiz == null) {
            if (respuesta_Si("Estas pensando en un animal? ")) {
                String animal = ventanaDialogo("Introduce el nombre del animal: ").toLowerCase();
                raiz = new Arbol(animal);
            } else {
                System.exit(0); //Finalizar el programa
            }
        }
        return raiz;
    }

    //Intenta adivinar el animal
    public static void adivinar(Arbol arbol) {
        //Verifica si el nodo actual es un nodo hoja, es decir, si no tiene hijos ni a la derecha ni a la izquierda
        if (arbol.getIzquierda() == null && arbol.getDerecha() == null) {
            //Si no tiene mas datos en la derecha o izquierda pregunta si es ese animal
            if (respuesta_Si("El animal es un/una: '" + arbol.getDato() + "'?")) {
                ventanaMensaje("Gane!");
            } else {
                aprender(arbol);
            }
        } else {
            //Pregunta por una característica del animal y avanza en el árbol según la respuesta, buscando el nodo correcto para adivinar
            if (respuesta_Si("El animal tiene la caracteristica: '" + arbol.getDato() + "'?")) {
                adivinar(arbol.getIzquierda());
            } else {
                adivinar(arbol.getDerecha());
            }
        }
    }

    //Asigna nuevos nodos al arbol, de esta forma aprende
    public static void aprender(Arbol arbol) {
        String nuevoAnimal = ventanaDialogo("Cual era el animal en el que estabas pensando?");
        String atributo = ventanaDialogo("Dame una caracteristica que tenga " + nuevoAnimal + " que no tenga " + arbol.getDato() + ": ");
        //asigna los datos al arbol
        arbol.setIzquierda(new Arbol(nuevoAnimal));
        arbol.setDerecha(new Arbol(arbol.getDato()));

        arbol.setDato(atributo);
        guardarDatosArbol(arbol);
    }

    public static void main(String[] args) {
        ventanaMensaje("***PIENSA EN UN ANIMAL***");
        Arbol raiz = raizArbol();
        while (true) {
            adivinar(raiz);
            if (!respuesta_Si("Quieres jugar de nuevo? ")) {
                break;
            }
        }
    }
}
