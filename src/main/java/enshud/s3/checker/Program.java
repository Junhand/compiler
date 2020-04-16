package enshud.s3.checker;

public class Program extends GetToken {

		private String name;
		private Blocks block;
		private CompoundStatement compoundStatement;

		public Program(String name, Blocks block, CompoundStatement compoundStatement, Records record){
			super(record);
			this.name=name;
			this.block=block;
			this.compoundStatement=compoundStatement;
		}

		public String getName(){
			return name;
		}

		public Blocks getBlock(){
			return block;
		}

		public CompoundStatement getCompoundStatement(){
			return compoundStatement;
		}
}
