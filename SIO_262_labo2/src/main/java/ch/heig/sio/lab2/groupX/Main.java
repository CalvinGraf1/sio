package ch.heig.sio.lab2.groupX;

import ch.heig.sio.lab2.coloring.Coloring;
import ch.heig.sio.lab2.coloring.ColoringAlgorithm;
import ch.heig.sio.lab2.graph.Graph;
import ch.heig.sio.lab2.graph.GraphReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public final class Main {

  public static void main(String[] args) throws IOException {
    Path dataDir = Path.of(args.length > 0 ? args[0] : "data");

    ColoringAlgorithm algorithm = new TrivialColoring();

    List<Path> files;
    try (Stream<Path> stream = Files.list(dataDir)) {
      files = stream.filter(p -> p.toString().endsWith(".txt")).sorted().toList();
    }

    for (Path file : files) {
      Graph graph;
      try {
        graph = GraphReader.read(file);
      } catch (IOException e) {
        System.err.println(file.getFileName() + " : " + e.getMessage());
        continue;
      }
      System.out.println(file.getFileName() + " " + graph);

      long start = System.nanoTime();
      Coloring coloring = algorithm.color(graph);
      long micros = (System.nanoTime() - start) / 1_000;

      System.out.printf("  %-16s %5d couleurs  %8d µs  %s%n",
          algorithm.name(), coloring.colorCount(), micros,
          coloring.isProperFor(graph) ? "OK" : "INVALIDE");
    }
  }
}
