package ch.heig.sio.lab2.graph;

import java.io.IOException;

/**
 * Exception levée en cas de détection d'un problème avec le format du fichier d'un graphe.
 * */
public class GraphFormatException extends IOException {
  public GraphFormatException(String message) {
    super(message);
  }
}
