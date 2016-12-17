/*******************************************************************************
 * KOMORAN 3.0 - Korean Morphology Analyzer
 *
 * Copyright 2015 Shineware http://www.shineware.co.kr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 	
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package kr.co.shineware.nlp.komoran.core.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.ObjectInputStream;

import kr.co.shineware.nlp.komoran.constant.FILENAME;
import kr.co.shineware.nlp.komoran.modeler.model.IrregularTrie;
import kr.co.shineware.nlp.komoran.modeler.model.Observation;
import kr.co.shineware.nlp.komoran.modeler.model.PosTable;
import kr.co.shineware.nlp.komoran.modeler.model.Transition;

public class Resources {
	private Transition transition;
	private Observation observation;
	private PosTable table;
	private IrregularTrie irrTrie;

	public Transition getTransition() {
		return transition;
	}
	public void setTransition(Transition transition) {
		this.transition = transition;
	}
	public Observation getObservation() {
		return observation;
	}
	public void setObservation(Observation observation) {
		this.observation = observation;
	}
	public PosTable getTable() {
		return table;
	}
	public void setTable(PosTable table) {
		this.table = table;
	}
	public IrregularTrie getIrrTrie() {
		return irrTrie;
	}
	public void setIrrTrie(IrregularTrie irrTrie) {
		this.irrTrie = irrTrie;
	}
	
	private void init(){
		this.table = null;
		this.observation = null;
		this.transition = null;
		this.irrTrie = null;

		this.table = new PosTable();
		this.observation = new Observation();
		this.transition = new Transition();
		this.irrTrie = new IrregularTrie();
	}
	
	public void load(ObjectInputStream ois_IrregularTrie, ObjectInputStream ois_observation, BufferedReader br_posTable, ObjectInputStream ois_transition) {
		this.init();
		this.table.load(br_posTable);
		this.observation.load(ois_observation);
		this.transition.load(ois_transition);
		this.irrTrie.load(ois_IrregularTrie);
		
		this.observation.getTrieDictionary().buildFailLink();
		this.irrTrie.getTrieDictionary().buildFailLink();		
	}
}
