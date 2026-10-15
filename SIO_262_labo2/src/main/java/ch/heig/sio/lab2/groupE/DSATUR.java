package ch.heig.sio.lab2.groupE;

import ch.heig.sio.lab2.coloring.Coloring;
import ch.heig.sio.lab2.graph.Graph;

public class DSATUR {

    private Graph graph;

    /**
     * Permet de colorier un graphe selon l'algorithme DSATUR
     * @param graph le graphe à colorier
     * @return le tableau de couleurs des sommets
     */
    public Coloring solve(Graph graph) {

        this.graph = graph;
        int nbVertices = graph.vertexCount();

        int[] colors = new int[nbVertices];
        int[] saturation = new int[nbVertices];
        int[]degrees = new int[nbVertices];

        // 0 = false (non coloré), 1 = true (coloré)
        int[][] neighboursColoring = new int[nbVertices][nbVertices];

        int nbColored = 0;
        int maxDegreeVertex;
        int uncoloredVertex = Integer.MAX_VALUE;
        int colorTested = 0;


        for (int v = 0; v < nbVertices; v++) {
            colors[v] = -1;
            saturation[v] = 0;

            // définit le degré du sommet
            degrees[v] = graph.degree(v);

            // initialisation du tableau des couleurs des voisins
            for (int c = 0; c < nbVertices; c++) {
                neighboursColoring[v][c] = 0;
            }
        }

        // par défaut le meilleur sommet est le sommet 0
        maxDegreeVertex = 0;

        // on recherche le sommet de degré max
        for(int v = 1; v < nbVertices; v++){
            if(degrees[v]> degrees[maxDegreeVertex]){
                maxDegreeVertex = v;
            }
        }

        colors[maxDegreeVertex] = 0;
        updateSaturation(maxDegreeVertex, 0, colors, neighboursColoring, saturation);
        nbColored++;

        // tant qu'on a des sommets pas coloriés
        while(nbColored<nbVertices){

            // sommet pas colorié
            uncoloredVertex=-1;

            // pour chaque sommet
            for(int v = 0; v < nbVertices; v++){

                // si le sommet n'est pas colorié
                if(colors[v] == -1){

                    // si on a pas encore de sommet à colorier ou que la saturation du sommet courant
                    // est plus grande que celle du sommet à colorier ou que la saturation du sommet courant est égale à
                    // celle du sommet à colorier et que le degré du sommet courant est plus grand que celui à colorier
                    if (uncoloredVertex==-1 || saturation[v] > saturation[uncoloredVertex]
                            || (saturation[v] == saturation[uncoloredVertex]
                            && degrees[v] > degrees[uncoloredVertex])){

                        // le nouveau sommet à colorier est le sommet courant
                        uncoloredVertex = v;
                    }
                }
            }

            colorTested = 0;

            // on regarde avec quelle couleur on peut colorier le sommet selon la couleur de ses voisins
            while(neighboursColoring[uncoloredVertex][colorTested] == 1){
                colorTested++;
            }
            // on colorie le sommet et on met à jour sa saturation ainsi que le nombre de sommets coloriés
            colors[uncoloredVertex] = colorTested;
            updateSaturation(uncoloredVertex, colorTested, colors, neighboursColoring, saturation);
            nbColored++;
        }

        return new Coloring(colors);

    }

    /**
     * Permet de mettre à jour la saturation des voisins du sommet colorié
     * @param vertex sommet colorié
     * @param color couleur du sommet
     * @param colors tableau de couleurs
     * @param neighboursColoring tableau de deux dimensions avec les couleurs des voisins
     * @param saturation degré de saturation des sommets
     */
    private void updateSaturation(int vertex, int color, int[] colors, int[][] neighboursColoring, int[] saturation){

        // récupère les voisins du sommet
        int[] vertexNeighbours = graph.neighbors(vertex);

        // pour chaque voisin du sommet colorié
        for(int v=0; v  < vertexNeighbours.length; v++){

            // si ce voisin n'est pas colorié et que la couleur du sommet colorié n'est pas déjà présente chez ce voisin
            if(colors[vertexNeighbours[v]] == -1 && neighboursColoring[vertexNeighbours[v]][color] == 0){
                saturation[vertexNeighbours[v]]++;
                neighboursColoring[vertexNeighbours[v]][color] = 1;
           }
        }
    }


}
