package ch.heig.sio.lab2.groupX;

import ch.heig.sio.lab2.coloring.Coloring;
import ch.heig.sio.lab2.coloring.ColoringAlgorithm;
import ch.heig.sio.lab2.graph.Graph;

/**
 * Exemple minimal : une couleur par sommet. Nul mais valide.
 */
public final class TrivialColoring implements ColoringAlgorithm {

  @Override
  public Coloring color(Graph graph) {
    int[] colors = new int[graph.vertexCount()];
    for (int v = 0; v < colors.length; v++) {
      colors[v] = v;
    }
    return new Coloring(colors);
  }
}
