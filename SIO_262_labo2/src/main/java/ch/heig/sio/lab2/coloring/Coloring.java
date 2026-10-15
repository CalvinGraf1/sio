package ch.heig.sio.lab2.coloring;

import ch.heig.sio.lab2.graph.Graph;

import java.util.Arrays;

/**
 * Solution d'un problème de coloration des sommets d'un graphe : {@code colors[v]} est la couleur du sommet {@code v}.
 * <p>
 * Les couleurs utilisées sont des entiers consécutifs, à partir de 0 et chaque sommet
 * est coloré. Un objet {@code Coloring} est immuable : le tableau reçu et le tableau renvoyé
 * sont des copies.
 */
public final class Coloring {

  /** Valeur à utiliser dans un tableau de travail pour un sommet pas encore coloré. */
  public static final int UNCOLORED = -1;

  /** Tableau des couleurs de chaque sommet */
  private final int[] colors;

  /** Nombre de couleurs, différentes, utilisées par la coloration */
  private final int colorCount;

  /**
   * Crée une coloration à partir d'un tableau de couleurs (le tableau est copié).
   *
   * @param colors couleur de chaque sommet, indexée par numéro de sommet
   * @throws IllegalArgumentException si une couleur est négative (sommet non coloré)
   *                                  ou si les couleurs utilisées ne sont pas consécutives à partir de 0
   */
  public Coloring(int[] colors) {
    this.colors = colors.clone();

    int max = -1;
    for (int c : this.colors) {
      if (c < 0) {
        throw new IllegalArgumentException("Incomplete coloring: negative color " + c);
      }
      max = Math.max(c, max);
    }

    boolean[] used = new boolean[max + 1];
    for (int c : this.colors) {
      used[c] = true;
    }
    for (int c = 0; c <= max; c++) {
      if (!used[c]) {
        throw new IllegalArgumentException(
            "Colors must be consecutive from 0: color " + c + " is unused but " + max + " is used");
      }
    }

    this.colorCount = max + 1;
  }

  /**
   * Renvoie une copie du tableau des couleurs.
   *
   * @return tableau {@code c} tel que {@code c[v]} est la couleur du sommet {@code v}
   */
  public int[] colors() {
    return colors.clone();
  }

  /**
   * Renvoie la couleur d'un sommet.
   *
   * @param vertex numéro du sommet, dans {@code [0, vertexCount() - 1]}
   * @return la couleur du sommet, dans {@code [0, colorCount() - 1]}
   * @throws IllegalArgumentException si le sommet est hors bornes
   */
  public int colorOf(int vertex) {
    checkVertex(vertex);
    return colors[vertex];
  }

  /**
   * @return le nombre de sommets colorés
   */
  public int vertexCount() {
    return colors.length;
  }

  /**
   * @return le nombre de couleurs utilisées (0 si aucun sommet)
   */
  public int colorCount() {
    return colorCount;
  }

  /**
   * Vérifie que cette coloration est valide pour le graphe donné.
   *
   * @param graph le graphe à vérifier
   * @return {@code true} si la coloration concerne ce graphe (même nombre de sommets)
   *         et qu'aucune arête n'a ses deux extrémités de même couleur
   */
  public boolean isProperFor(Graph graph) {
    if (graph.vertexCount() != colors.length) {
      return false;
    }
    for (int u = 0; u < colors.length; u++) {
      for (int i = 0; i < graph.degree(u); i++) {
        if (colors[u] == colors[graph.neighbor(u, i)]) {
          return false;
        }
      }
    }
    return true;
  }

  private void checkVertex(int v) {
    if (v < 0 || v >= colors.length) {
      throw new IllegalArgumentException(
          "Vertex " + v + " out of bounds [0, " + (colors.length - 1) + "]");
    }
  }

  /** Deux colorations sont égales si elles attribuent les mêmes couleurs aux mêmes sommets. */
  @Override
  public boolean equals(Object o) {
    return o instanceof Coloring other && Arrays.equals(colors, other.colors);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(colors);
  }

  @Override
  public String toString() {
    return "Coloring[" + colorCount + " colors, " + Arrays.toString(colors) + "]";
  }
}