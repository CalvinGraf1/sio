package ch.heig.sio.lab2.coloring;

import ch.heig.sio.lab2.graph.Graph;

/**
 * Heuristique de coloration des sommets d'un graphe : calcule une coloration
 * propre (deux sommets adjacents n'ont jamais la même couleur) et complète (tous les
 * sommets sont colorés).
 */
public interface ColoringAlgorithm {

  /**
   * Calcule une coloration propre et complète du graphe.
   *
   * @param graph le graphe à colorer
   * @return une coloration pour laquelle {@link Coloring#isProperFor(Graph)} est vrai
   */
  Coloring color(Graph graph);

  /**
   * Nom de l'algorithme. Par défaut, le no simple de la classe ; peut être redéfini.
   *
   * @return le nom de l'algorithme
   */
  default String name() {
    return getClass().getSimpleName();
  }
}