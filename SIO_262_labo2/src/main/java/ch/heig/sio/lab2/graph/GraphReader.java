package ch.heig.sio.lab2.graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Classe utilitaire permettant la lecture d'un graphe simple non orienté depuis un fichier texte :
 * première ligne "n m", puis m lignes "u v" (sommets numérotés de 0 à n-1).
 */
public final class GraphReader {

  private GraphReader() {
  }

  public static Graph read(Path file) throws IOException {
    try (BufferedReader reader = Files.newBufferedReader(file)) {
      return read(reader);
    }
  }

  public static Graph read(BufferedReader reader) throws IOException {
    int n = 0, m = 0;
    int[][] edges = null;   // tableau des arêtes, null tant que l'en-tête n'a pas été lue
    int edgeCount = 0;
    int lineNo = 0;
    String line;

    while ((line = reader.readLine()) != null) {
      lineNo++;
      line = line.strip();
      if (line.isEmpty()) {
        continue;
      }
      int[] pair = parsePair(line, lineNo);

      if (edges == null) {                        // lecture de l'en-tête
        n = pair[0];
        m = pair[1];
        if (n < 1) {
          throw error(lineNo, "n must be at least 1");
        }
        if (m < 0) {
          throw error(lineNo, "m must be non-negative");
        }        if (m > (long) n * (n - 1) / 2) {
          throw error(lineNo, "too many edges (" + m + ") for " + n + " vertices");
        }
        edges = new int[m][];
      } else {                                    // lecture d'une arête
        if (edgeCount == m) {
          throw error(lineNo, "more edges than announced (" + m + ")");
        }
        int u = pair[0], v = pair[1];
        if (u < 0 || u >= n || v < 0 || v >= n) {
          throw error(lineNo, "vertex out of bounds [0, " + (n - 1) + "]");
        }
        if (u == v) {
          throw error(lineNo, "loop on vertex " + u);
        }
        edges[edgeCount++] = pair;
      }
    }

    if (edges == null) {
      throw new GraphFormatException("Empty file: missing header 'n m'");
    }
    if (edgeCount < m) {
      throw new GraphFormatException(
          "Expected " + m + " edges but found only " + edgeCount);
    }

    try {
      return new Graph(n, edges);   // détecte boucles, doublons, ...
    } catch (IllegalArgumentException e) {
      throw new GraphFormatException(e.getMessage());
    }
  }

  private static int[] parsePair(String line, int lineNo) throws GraphFormatException {
    String[] tokens = line.split("\\s+");
    if (tokens.length != 2) {
      throw error(lineNo, "expected exactly two integers, got '" + line + "'");
    }
    try {
      return new int[]{Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1])};
    } catch (NumberFormatException e) {
      throw error(lineNo, "not an integer in '" + line + "'");
    }
  }

  private static GraphFormatException error(int lineNo, String message) {
    return new GraphFormatException("Line " + lineNo + ": " + message);
  }
}
