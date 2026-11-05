package ch.heig.sio.lab2.groupE;

import ch.heig.sio.lab2.coloring.Coloring;
import ch.heig.sio.lab2.coloring.ColoringAlgorithm;
import ch.heig.sio.lab2.graph.Graph;

import java.util.Arrays;

/**
 * Implémentation de l'heuristique Largest First (LF).
 *
 * Les sommets sont triés par degré décroissant à l'aide
 * du tri par comptage, puis colorés dans cet ordre.
 */
public final class LF implements ColoringAlgorithm {

    @Override
    public Coloring color(Graph graph) {
        int n = graph.vertexCount();

        // Calculer le degré de chaque sommet
        // et construire les clés utilisées pour le tri.
        int[] degrees = new int[n];
        int[] keys = new int[n];
        int[] count = new int[n];

        for (int v = 0; v < n; v++) {
            degrees[v] = graph.degree(v);

            // Inverser la clé pour obtenir les degrés décroissants
            // avec le tri par comptage croissant.
            keys[v] = (n - 1) - degrees[v];

            // Compter le nombre de sommets pour chaque clé.
            count[keys[v]]++;
        }

        // Transformer Count en tableau de fréquences cumulées.
        for (int k = 1; k < n; k++) {
            count[k] += count[k - 1];
        }

        // Construire le tableau des sommets triés.
        int[] order = new int[n];

        // Parcourir les sommets en ordre inverse pour garantir
        // la stabilité du tri par comptage.
        for (int v = n - 1; v >= 0; v--) {
            int key = keys[v];

            count[key]--;
            order[count[key]] = v;
        }

        // -1 signifie que le sommet n'est pas encore coloré.
        int[] colors = new int[n];
        Arrays.fill(colors, -1);

        // Pour chaque couleur, mémoriser le compteur du sommet
        // pour lequel cette couleur est indisponible.
        int[] indispo = new int[n];

        // Identifiant du sommet courant, initialisé à 1.
        int c = 1;

        // Colorer les sommets dans l'ordre LF.
        for (int u : order) {

            // Marquer les couleurs utilisées par les voisins déjà colorés.
            for (int i = 0; i < degrees[u]; i++) {
                int v = graph.neighbor(u, i);

                if (colors[v] != -1) {
                    indispo[colors[v]] = c;
                }
            }

            // Rechercher la première couleur disponible.
            int k = 0;
            while (k < n && indispo[k] == c) {
                k++;
            }

            // Un sommet ne peut avoir plus de n - 1 voisins :
            // une couleur parmi les n couleurs possibles est disponible.
            if (k == n) {
                throw new IllegalStateException(
                        "Aucune couleur disponible pour le sommet " + u
                );
            }

            // Attribuer la couleur au sommet courant.
            colors[u] = k;

            // Passer à l'identifiant suivant.
            c++;
        }

        // Retourner la coloration calculée.
        return new Coloring(colors);
    }

    @Override
    public String name() {
        return "LF";
    }
}

