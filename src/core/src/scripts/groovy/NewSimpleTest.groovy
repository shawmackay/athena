class Simple{
	Simple(){
		println("Inst'ed");
	}
	doAdd(a,b){
		return a+b;
	}

	goOver(c){
		for (ob in c){
			println("${ob}");
		}
	}
}

println(command.getCallName());
println("Connection InTxn?: " + connection.inTransaction());
a = new Simple();
println(a.doAdd(2,3));
a.goOver((1..4).toArray());