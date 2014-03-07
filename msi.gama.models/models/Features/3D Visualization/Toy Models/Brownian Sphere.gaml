model brownianSphere   

global {
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 1000 category: 'Initialization';
	int width_and_height_of_environment parameter: 'Dimensions' min: 10 <- 1000 category: 'Initialization';  
	int radius parameter: 'Radius' min: 10 <- 10 ;
	
	list blueCombination <- [([0,113,188]),([68,199,244]),([157,220,249]),([212,239,252])];
	geometry shape <- square(width_and_height_of_environment);
	
	init { 
		create cells number: number_of_agents { 
			location <- {rnd(width_and_height_of_environment), rnd(width_and_height_of_environment), rnd(width_and_height_of_environment)};
			color <- rgb((blueCombination)[rnd(3)]);
		} 
	}  
} 
    
species cells skills: [moving] {  
	rgb color;
	list<cells> neighbours update: neighbours(width_and_height_of_environment/10);
	reflex move {
		  do wander_3D z_max:width_and_height_of_environment;
	}	
	
	list<cells> neighbours(int dist) {
                return cells select ((each distance_to self) < dist);
    }
     	
	aspect default {
		  draw sphere(radius);	
    }
    
    aspect neighbours {
		draw sphere(radius);
		loop pp over: neighbours {
			draw line([self.location,pp.location]);
		}	
    }
}
	

experiment Display  type: gui {
	output {
		display WanderingSphere type:opengl  background:rgb(10,40,55){
			species cells;
		}
		display WanderingSphereWithNeighbourgs type:opengl  background:rgb(10,40,55){
			species cells aspect:neighbours;
		}
	}
}


