/**
 *  classicgraphgeneration
 *  Author: Patrick Taillandier
 *  Description: Show how to create scale-free/small-world/complete graphs
 */

model classicgraphgeneration

global {
	graph the_graph ;
	string graph_type <- "scale-free";
	int nb_nodes <- 20;
	float p <- 0.3;
	int k <- 2;
	int m <- 5;
	
	init {
		switch graph_type {
			match "scale-free" {
				the_graph <- generate_barabasi_albert(node, edge, nb_nodes,m);	
			}
			match "small-world" {
				the_graph <- generate_watts_strogatz(node, edge, nb_nodes, p, k);	
			}
			match "complete" {
				the_graph <- generate_complete_graph(node, edge, nb_nodes);	
			}	
		}
	}
	
}

species edge {
	aspect default {	
		draw shape color: rgb("black");
	}
}

species node {
	aspect default {	
		draw circle(1) color: rgb("red");
	}
}

experiment loadgraph type: gui {
	parameter "Graph type" var: graph_type among: [ "scale-free", "small-world", "complete"];
	parameter "Number of nodes" var: nb_nodes min: 5 max: 100;
	parameter "Probability to rewire an edge (beta)" var: p min: 0.0 max: 1.0 category: "small-world";
	parameter "Base degree of each node. k must be even" var: k min: 2 max: 10 category: "small-world";
	parameter "Number of edges added per novel node" var: m min: 1 max: 10 category: "scale-free";
	
	output {
		display map type: opengl{
			species edge ;
			species node ;
		}
	}
}