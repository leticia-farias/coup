package coup.controller; // mover dps

import coup.acoes.FactoryVersaoInquisidor;
import coup.acoes.FactoryVersaoOriginal;

public class Partida {
	private int modoJogo;
	
	// atributos opcionais
	private final boolean ativarHeranca; 

	private Partida(PartidaBuilder builder) {
		this.ativarHeranca = builder.ativarHeranca;
		this.modoJogo = builder.modoJogo;
	}
	
	public static class PartidaBuilder {
		private int modoJogo;
		private boolean ativarHeranca;

		public PartidaBuilder(int modoJogo) {
			this.modoJogo = modoJogo;
		}

		public PartidaBuilder setAtivarHeranca(boolean ativarHeranca) {
			this.ativarHeranca = ativarHeranca;
			return this;
		}
		
		public Partida build() {
//			Verificar conflitos de expansão (ex burocrata e doacao ativadas, lança uma exceção que não podem jogar juntas)
			
			if (this.modoJogo == 1) {
				new FactoryVersaoOriginal();
			} else {				
				new FactoryVersaoInquisidor();
			}
			return new Partida(this);
		}
	}
	
}
