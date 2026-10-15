package ch.heig.sio.lab2.graph;

import java.util.Arrays;

/**
 * Graphe simple et non orienté, immuable. Les sommets sont numérotés de {@code 0} à {@code n-1}.
 * <p>
 * Le graphe est stocké sous forme de listes d'adjacence (tableau de tableaux), chaque liste
 * étant triée par ordre croissant. Les boucles et les arêtes multiples sont refusées à la
 * construction.
 * <p>
 * Aucun tableau interne n'est exposé : le parcours des voisins se fait soit élément par
 * élément ({@link #degree(int)} et {@link #neighbor(int, int)}, sans copie), soit via une
 * copie ({@link #neighbors(int)}).
 */
public final class Graph {

  private final int[][] adjacency;
  private final int edgeCount;

  /**
   * Construit un graphe à partir d'un tableau d'arêtes {@code edges} (qui n'est pas
   * conservé).
   *
   * @param vertexCount nombre de sommets {@code n} (numérotés de {@code 0} à  {@code n-1})
   * @param edges       tableau d'arêtes, chacune sous la forme {@code {u, v}}
   * @throws IllegalArgumentException si {@code vertexCount} est plus petit que 1, si une arête n'a pas
   *                                  exactement deux extrémités, si un sommet est hors bornes,
   *                                  s'il y a une boucle ou si une arête apparaît plusieurs fois
   *                                  (dans un sens ou dans l'autre)
   */
  public Graph(int vertexCount, int[][] edges) {
    if (vertexCount < 1) {
      throw new IllegalArgumentException("Vertex count must be at least 1: " + vertexCount);
    }

    // 1) Validation et calcul des degrés
    int[] degrees = new int[vertexCount];
    for (int[] edge : edges) {
      if (edge.length != 2) {
        throw new IllegalArgumentException("An edge must have exactly 2 endpoints");
      }
      int u = edge[0], v = edge[1];
      checkVertex(u, vertexCount);
      checkVertex(v, vertexCount);
      if (u == v) {
        throw new IllegalArgumentException("Loop on vertex " + u);
      }
      degrees[u]++;
      degrees[v]++;
    }

    // 2) Remplissage des tableaux de voisins
    adjacency = new int[vertexCount][];
    for (int v = 0; v < vertexCount; v++) {
      adjacency[v] = new int[degrees[v]];
    }
    int[] filled = new int[vertexCount];
    for (int[] edge : edges) {
      int u = edge[0], v = edge[1];
      adjacency[u][filled[u]++] = v;
      adjacency[v][filled[v]++] = u;
    }

    // 3) Tri des voisins, pourrait être fait en O(n+m), permet la détection des doublons (et areAdjacent en O(log deg))
    for (int v = 0; v < vertexCount; v++) {
      Arrays.sort(adjacency[v]);
      for (int i = 1; i < adjacency[v].length; i++) {
        if (adjacency[v][i] == adjacency[v][i - 1]) {
          throw new IllegalArgumentException(
              "Duplicate edge {" + v + ", " + adjacency[v][i] + "}");
        }
      }
    }
    edgeCount = edges.length;
  }

  /**
   * @return le nombre de sommets {@code n} du graphe
   */
  public int vertexCount() {
    return adjacency.length;
  }

  /**
   * @return le nombre d'arêtes {@code m} du graphe
   */
  public int edgeCount() {
    return edgeCount;
  }

  /**
   * Renvoie le degré d'un sommet.
   *
   * @param v numéro du sommet, dans {@code [0, n-1]}
   * @return le degré de {@code v}
   * @throws IllegalArgumentException si {@code v} est hors bornes
   */
  public int degree(int v) {
    checkVertex(v, adjacency.length);
    return adjacency[v].length;
  }

  /**
   * Renvoie le {@code i}-ème voisin d'un sommet. Les voisins sont triés par
   * ordre croissant. À utiliser dans les boucles où la performance compte :
   * <pre>{@code
   * for (int i = 0; i < graph.degree(v); i++) {
   *   int w = graph.neighbor(v, i);
   * }
   * }</pre>
   *
   * @param v numéro du sommet, dans {@code [0, n-1]}
   * @param i indice du voisin, dans {@code [0, degree(v) - 1]}
   * @return le numéro du {@code i}-ème voisin de {@code v}
   * @throws IllegalArgumentException       si {@code v} est hors bornes
   * @throws ArrayIndexOutOfBoundsException si {@code i} est hors bornes
   */
  public int neighbor(int v, int i) {
    checkVertex(v, adjacency.length);
    return adjacency[v][i];
  }

  /**
   * Renvoie une copie de la liste des voisins d'un sommet, triée par ordre croissant.
   * Chaque appel alloue un nouveau tableau.
   *
   * @param v numéro du sommet, dans {@code [0, n-1]}
   * @return un nouveau tableau contenant les voisins de {@code v}
   * @throws IllegalArgumentException si {@code v} est hors bornes
   */
  public int[] neighbors(int v) {
    checkVertex(v, adjacency.length);
    return adjacency[v].clone();
  }

  /**
   * Teste si deux sommets sont adjacents (recherche dichotomique, O(log degré)).
   *
   * @param u numéro d'un sommet, dans {@code [0, n-1]}
   * @param v numéro d'un sommet, dans {@code [0, n-1]}
   * @return {@code true} si {@code {u, v}} est une arête du graphe
   * @throws IllegalArgumentException si {@code u} ou {@code v} est hors bornes
   */
  public boolean areAdjacent(int u, int v) {
    checkVertex(u, adjacency.length);
    checkVertex(v, adjacency.length);
    return Arrays.binarySearch(adjacency[u], v) >= 0;
  }

  /**
   * @return le plus grand degré du graphe (0 si le graphe n'a pas d'arêtes)
   */
  public int maxDegree() {
    int max = 0;
    for (int[] list : adjacency) {
      max = Math.max(max, list.length);
    }
    return max;
  }

  @Override
  public String toString() {
    return "Graph[n=" + vertexCount() + ", m=" + edgeCount + "]";
  }

  private static void checkVertex(int v, int n) {
    if (v < 0 || v >= n) {
      throw new IllegalArgumentException("Vertex " + v + " out of bounds [0, " + (n - 1) + "]");
    }
  }
}
