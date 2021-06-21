import java.util.*;

public class PilaArena {

	int[][] tablero; 
	
	//Constructor, dado un numero de granos de arena inicializa el tablero
	//con las dimensiones adecuadas
	public PilaArena(int N) {
		tablero = crearTablero(N);
	}
	
	//Metodo estático, calcula las dimensiones mínimas que pueda tener una matriz
	//para una cantidad de granos de arena, considerando que a lo más habrá un
	// grano de arena por cuadrado (retorna un int)
	public static int dimensionMatriz(int granos) {
		//dim: Dimensiones de la matriz
		double dim = Math.sqrt(granos); //A lo más un grano de arena por cuadrado
		dim = Math.ceil(dim);
		if (dim % 2 == 0) {
			dim++; //Preferir tableros de dimensión impar por conveniencia
		}
		return (int) dim;
	}
		
	//Metodo estatico, inicializa el tablero dado un N 
	//(retorna matriz de int)
	public static int[][] crearTablero(int N){
		int dim = dimensionMatriz(N);
		int[][] matriz = new int[(int) dim][(int) dim];
		//Ubicar los N granos en el centro de la matriz
		matriz[(int) (dim) / 2 ][(int) (dim) / 2 ] = N; 
		return matriz;
	}
	
	//Metodo de instancia, resuelve el derrumbe de una pila de arena
	// en un tablero inicializado y retorna el número de veces
	//que se aplicó la regla de los 4 granos (retorna un int)
	public int resolver() {
		int conteo = resolverOctavo(tablero);
		completarTablero(tablero);
		return conteo;
	}
	
	//Metodo estatico, completa el resto del tablero asumiendo que ya tiene un
	//octavo resuelto (no retorna nada)
	public static void completarTablero(int[][] superficie) {
		//Completar un cuarto del tablero
		for(int i = 0; i <= superficie.length/2; i++) {
			for(int j = 0; j < i; j++) {
				superficie[i][j] = superficie[j][i];
			}
		}
		//Completar el resto
		for(int i = 0; i <= superficie.length/2; i++) {
			for(int j = 0; j <= superficie.length/2; j++) {
				//Simetría con respecto al eje vertical
				superficie[i][j+((superficie.length/2-j)*2)] = superficie[i][j];
				//Simetria con respecto al eje horizontal
				superficie[i+((superficie.length/2-i)*2)][j] = superficie[i][j];
				//Simetria con respecto al centro
				superficie[i+((superficie.length/2-i)*2)][j+((superficie.length/2-j)*2)] = superficie[i][j];
			}
		}
	}
	
	//Metodo estatico, resuelve sólo un octavo del tablero inicializado,
	//retornando el numero de veces que se aplicó la regla de los
	//4 granos (retorna un int)
	public static int resolverOctavo(int[][] superficie) {
		
		//Crear un contador de iteraciones para estimar el tamaño máximo
		//de la matriz en cada iteración
		int it = 0; 
		
		//Guardar índice de la fila y columna central de la matriz
		int centro = superficie.length/2;
		
		//Crear un contador de veces que se aplicó la regla de los 4 granos
		int cont = 0; 
		
		//Crear un booleano que detrmine si se ha hecho una operación en
		//la iteración actual
		boolean op = true;
		
		while(op) {
			//Resolver el centro del tablero
			op = resolverCentro(superficie);
			if(op) {
				cont++;
				it++;
			}
			
			//Calcular dimension de la matriz a calcular para la iteracion
			//actual (al principio no es necesario revisar todo el tablero)
			////////////////OPTIMIZACION: Con cada iteracion, ahora aumenta en 
			////////////////2 la dimensión
			int dim_menor = 2*it + 1;
			int dim = dim_menor > superficie.length ? superficie.length : dim_menor;
			
			//Resolver resto de los puntos
			for (int i = (centro - dim/2); i < centro; i++) {
				for(int j = i; j <= centro; j++) {
					if(superficie[i][j] >= 4) {
						////////superficie[i][j] -= 4; 
						cont++; 
						op = true;
						
						////////NUEVA LINEA
						int suma = superficie[i][j]/4; //Valor a sumar a los vecinos
						
						//////CAMBIAR +=1 POR +=suma
						superficie[i-1][j]+=suma;//Todos los puntos realizan este paso
						//Resolver diagonal (primer borde del octavo)
						if (i == j) {
							superficie[i][j+1] += suma;	
							}
						//Resolver columna central del tablero (segundo borde)
						else if(j == centro) {
							superficie[i][j-1] += suma;
							superficie[i+1][j] += suma;
						}
						//Resolver resto de los puntos
						else {
							superficie[i+1][j] += suma;
							superficie[i][j+1] += suma;
							superficie[i][j-1] += suma;
						}
						//Resolver casos especiales donde, por simetría, se sume
						//más de 1 a algún espacio.
						//CASO 1: Se suma 1 a la columna central del tablero
						if(j == (centro -1)) 
							superficie[i][j+1] += suma;
						//CASO 2: Se suma 1 a la diagonal
						if(j == i+1) {
							superficie[i][j-1] += suma;
							if(j == centro) //Sub-caso: Si se le suma al centro
								superficie[i+1][j] += suma * 3;
							else
								superficie[i+1][j] += suma;
						}
						//////NUEVA LINEA
						superficie[i][j] = superficie[i][j] % 4;
					}
				}
			}
		}
		return cont;
	}
	
	//Metodo estático, resuelve el punto central del tablero una vez, retornando
	//TRUE si hubo un derrumbe y FALSE si no (retorna un booleano)
	public static boolean resolverCentro(int[][] superficie) {
		//Resolver centro, ubicado en [length/2][length/2] ya que las dimensiones
		//de la matriz son impares, pero la indexación comienza en 0
		if(superficie[superficie.length/2][superficie.length/2] >= 4) {
			//////NUEVA LINEA
			int suma = superficie[superficie.length/2][superficie.length/2]/4;
			
			//////CAMBIAR +=1 POR +=suma
			superficie[superficie.length/2 - 1][superficie.length/2] += suma;
			superficie[superficie.length/2][superficie.length/2] = superficie[superficie.length/2][superficie.length/2] % 4;
			return true;
		}
		else
			return false;
	}
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		//Se pide al usuario ingresar un valor para N
		System.out.println("N?"); 
		int n = sc.nextInt();
		
		//Registrar el tiempo de ejecución del programa
		long Start = System.currentTimeMillis();
		PilaArena pila = new PilaArena(n);
		int count = pila.resolver();
		
		Ventana miVentana = new Ventana(700, "Visualizacion pila de arena");
		miVentana.mostrarMatriz(pila.tablero);
		
		long End = System.currentTimeMillis();
		System.out.println("Numero total de veces que se aplicó la regla: " + count);
		System.out.println("Tiempo de ejecución (en milisegundos): " + (End-Start));
	}
}
