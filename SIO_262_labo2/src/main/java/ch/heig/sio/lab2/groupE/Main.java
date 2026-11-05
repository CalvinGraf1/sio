package ch.heig.sio.lab2.groupE;

import ch.heig.sio.lab2.coloring.Coloring;
import ch.heig.sio.lab2.graph.Graph;
import ch.heig.sio.lab2.graph.GraphReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public final class Main {

  // Exécutions d'échauffement de la JVM, non comptabilisées.
  private static final int WARMUP = 5;
  // Exécutions mesurées, on reporte la moyenne.
  private static final int REPS = 20;

  public static void main(String[] args) throws IOException {
    Path dataDir = Path.of(args.length > 0 ? args[0] : "data");

    List<Path> files;
    try (Stream<Path> stream = Files.list(dataDir)) {
      files = stream.filter(p -> p.toString().endsWith(".txt")).sorted().toList();
    }

    System.out.printf("Échauffement : %d, répétitions : %d%n", WARMUP, REPS);
    System.out.printf("%-12s %6s %9s %6s %6s | %6s %6s %7s | %10s %10s %8s | %s%n",
            "Instance", "n", "m", "Δ", "D", "LF", "DSATUR", "gain", "LF [ms]", "DS [ms]", "augm.", "Valide");

    for (Path file : files) {
      Graph graph;
      try {
        graph = GraphReader.read(file);
      } catch (IOException e) {
        System.err.println(file.getFileName() + " : " + e.getMessage());
        continue;
      }

      String name = file.getFileName().toString().replaceFirst("\\.txt$", "");
      int n = graph.vertexCount();
      int m = graph.edgeCount();

      double density = n < 2 ? 0 : 2.0 * m / ((double) n * (n - 1));

      LF lf = new LF();
      DSATUR dsatur = new DSATUR();
      Result resultLF = measure(graph, lf::color);
      Result resultDSATUR = measure(graph, dsatur::color);

      int colorsLF = resultLF.coloring().colorCount();
      int colorsDSATUR = resultDSATUR.coloring().colorCount();
      double colorGain = 100.0 * (colorsLF - colorsDSATUR) / colorsLF;
      double timeIncrease = 100.0 * (resultDSATUR.meanMs() - resultLF.meanMs()) / resultLF.meanMs();
      boolean valid = resultLF.coloring().isProperFor(graph) && resultDSATUR.coloring().isProperFor(graph);

      System.out.printf("%-12s %6d %9d %6d %6.3f | %6d %6d %6.2f%% | %10.3f %10.3f %7.0f%% | %s%n",
              name, n, m, graph.maxDegree(), density,
              colorsLF, colorsDSATUR, colorGain,
              resultLF.meanMs(), resultDSATUR.meanMs(), timeIncrease,
              valid ? "OK" : "INVALIDE");
    }
  }

  // Coloration obtenue et temps moyen d'un appel en millisecondes.
  private record Result(Coloring coloring, double meanMs) {}

  // Mesure uniquement l'appel à l'algorithme
  private static Result measure(Graph graph, Function<Graph, Coloring> algorithm) {
    Coloring coloring = null;
    for (int i = 0; i < WARMUP; i++) {
      coloring = algorithm.apply(graph);
    }
    long total = 0;
    for (int i = 0; i < REPS; i++) {
      long start = System.nanoTime();
      coloring = algorithm.apply(graph);
      total += System.nanoTime() - start;
    }

    if (!coloring.isProperFor(graph)) {
      throw new IllegalStateException("Coloration invalide");
    }

    return new Result(coloring, total / 1e6 / REPS);
  }
}