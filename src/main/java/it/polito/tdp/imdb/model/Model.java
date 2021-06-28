package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {

	private SimpleWeightedGraph <Director, DefaultWeightedEdge> grafo;
	private ImdbDAO dao;
	private Map <Integer, Director> idMap;
	
	private List<Director> percorsoMigliore;
	public double sommaPesi;
	
	public Model() {
		dao = new ImdbDAO();
		idMap = new HashMap<Integer, Director>();
		dao.listAllDirectors(idMap);
	}
	
	
	public void creaGrafo(int anno) {
		grafo = new SimpleWeightedGraph <>(DefaultWeightedEdge.class);
		
		
		// aggiungo i vertici
		Graphs.addAllVertices(grafo, dao.getVertici(anno, idMap));
		
		// aggiungo gli archi
		for(Adiacenza a: dao.getAdiacenze(anno, idMap)) {
			if(this.grafo.containsVertex(a.getD1()) && this.grafo.containsVertex(a.getD2())) {
				DefaultWeightedEdge e = this.grafo.getEdge(a.getD1(), a.getD2());
				if(e==null) {
					Graphs.addEdgeWithVertices(grafo, a.getD1(), a.getD2(), a.getPeso());
				}
			}
		}
	}


	public int getNumVertici() {
		if(this.grafo!=null) {
			return this.grafo.vertexSet().size();
		}
		return 0;
	}
	
	public int getNumArchi() {
		if(this.grafo!=null) {
			return this.grafo.edgeSet().size();
		}
		return 0;
	}
	
	public Set<Director> getVerticiTendina(){
		return this.grafo.vertexSet();
	}
	
	// metodo per trovare i registi adiacenti
	public List<RegistaAdiacente> getRegistiAdiacenti (Director d){
		
		List<RegistaAdiacente> result = new LinkedList<RegistaAdiacente>();
		
		for (Director vicino: Graphs.neighborListOf(grafo, d)) {
			DefaultWeightedEdge e = this.grafo.getEdge(vicino, d);
			double peso = this.grafo.getEdgeWeight(e);
			RegistaAdiacente ra = new RegistaAdiacente (vicino, peso);
			result.add(ra);
		}
		Collections.sort(result, new Comparator<RegistaAdiacente>() {

			@Override
			public int compare(RegistaAdiacente o1, RegistaAdiacente o2) {
				Double d1 = o1.getPeso();
				Double d2 = o2.getPeso();
				return d2.compareTo(d1);
			}
			
		});
		
		return result;
	}
	
	public List<Director> trovaPercorso (Director partenza, int c ){
		this.percorsoMigliore = new ArrayList<>();
		List<Director> parziale = new ArrayList<>();
		sommaPesi = 0;
		
		parziale.add(partenza);
		cerca(c, parziale);
		return this.percorsoMigliore;
	}


	private void cerca(int c, List<Director> parziale) {

		// caso terminale 
		if (sommaPesi > c) {
			return ;
		}
		if (parziale.size()>this.percorsoMigliore.size()) {
			this.percorsoMigliore = new ArrayList<>(parziale);
			// 	return ;
		}
			if(this.percorsoMigliore == null) {
				this.percorsoMigliore = new ArrayList<>(parziale);
				return ;
			}
		
		
		// altrimenti ..
		Director ultimo = parziale.get(parziale.size()-1);
		
		
		for (Director vicino : Graphs.neighborListOf(grafo, ultimo)) {
			DefaultWeightedEdge e = this.grafo.getEdge(vicino, ultimo);
			double peso = this.grafo.getEdgeWeight(e);
			sommaPesi += peso;
			
		
				if(!parziale.contains(vicino)) {
					parziale.add(vicino);
					cerca(c, parziale);
					parziale.remove(parziale.size()-1);
					sommaPesi -= peso;
			
			}
		}
		
	}
}