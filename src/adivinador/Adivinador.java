package adivinador;

import java.io.*;
import javax.swing.*;
import java.util.Scanner;
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

    public static String ventanaOpciones(String txt) {
        String[] opciones = {"Si", "No"};
        int opcionSeleccionada = JOptionPane.showOptionDialog(null, txt, "Adivinador", 0, JOptionPane.QUESTION_MESSAGE, null, opciones, "");
        return opciones[opcionSeleccionada];
    }

    public static String ventanaDialogo(String txt) {
        String dato = JOptionPane.showInputDialog(txt);
        return dato;
    }

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

    //Crea nodos nuevos por cada reepuesta que se de
    public static Arbol crearRaizArbol() {
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
        if (arbol.getIzquierda() == null && arbol.getDerecha() == null) {
            if (respuesta_Si("Adivine! Es un " + arbol.getDato() + "?")) {
                ventanaMensaje("Gane!");
            } else {
                aprender(arbol);
            }
        } else {
            if (respuesta_Si("El animal que estas pensando tiene la caracteristica: '" + arbol.getDato() + "'?")) {
                adivinar(arbol.getIzquierda());
            } else {
                adivinar(arbol.getDerecha());
            }
        }
    }

    //Asigna los nodos a la derecha o a la izquierda dependiendo de la respuesta, de esta forma aprende
    public static void aprender(Arbol arbol) {
        String nuevoAnimal = ventanaDialogo("Cual era el animal que estabas pensando? ");
        String atributo = ventanaDialogo("Dame una caracteristica que distinga un " + arbol.getDato() + " de un " + nuevoAnimal + ": ");
        if (respuesta_Si("Si el animal fuera un " + arbol.getDato() + ", que respuesta seria?")) {
            arbol.setIzquierda(new Arbol(arbol.getDato()));
            arbol.setDerecha(new Arbol(nuevoAnimal));
        } else {
            arbol.setIzquierda(new Arbol(nuevoAnimal));
            arbol.setDerecha(new Arbol(arbol.getDato()));
        }
        arbol.setDato(atributo);
        guardarDatosArbol(arbol);
    }

    public static void main(String[] args) {
        ventanaMensaje("***PIENSA EN UN ANIMAL***");
        Arbol raiz = crearRaizArbol();
        while (true) {
            adivinar(raiz);
            if (!respuesta_Si("Quieres jugar de nuevo? ")) {
                break;
            }
        }
    }
}
