import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import java.util.*;
import java.awt.image.*;

public class GamePanel extends Canvas implements Runnable {
	private static final int PWIDTH = 960, PHEIGHT = 800;
	private Thread animator;
	private boolean running = false;

	int FPS, SFPS;
	float zoom = 1f;
	double posx, posy;
	int ntileW = 60, ntileH = 50;

	boolean LEFT, RIGHT, UP, DOWN;
	public static int mousex, mousey;
	public static Random rnd = new Random();
	public static ArrayList<Agente> listadeagentes = new ArrayList<>();

	Mapa_Grid mapa;
	MeuAgente meuHeroi;
	int[] caminho;
	int caminhoIndex = 0;
	Font f = new Font("", Font.BOLD, 20);
	HashSet<Integer> nodosPercorridos = new HashSet<>();
	LinkedList<Nodo> pilhaprofundidade = new LinkedList<>();

	// Tipo de heurística: 0 = Manhattan, 1 = Euclidiana
	private int tipoHeuristica = 0;

	public GamePanel() {
		setupPanel();
		setupListeners();
		initializeGame();
	}

	private void setupPanel() {
		setBackground(Color.white);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
		setFocusable(true);
		requestFocus();
	}

	private void resetJogo() {
		meuHeroi.X = 0 * 16 + 8; // Calculate the X position based on the tile size (X * 16 + 8)
		meuHeroi.Y = 0 * 16 + 8; // Calculate the Y position based on the tile size (Y * 16 + 8)
		caminho = null;
		caminhoIndex = 0;
		nodosPercorridos.clear();
		posx = 0;
		posy = 0;
	}

	private void setupListeners() {
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				// Arrow keys
				if (key == KeyEvent.VK_LEFT)
					LEFT = true;
				if (key == KeyEvent.VK_RIGHT)
					RIGHT = true;
				if (key == KeyEvent.VK_UP)
					UP = true;
				if (key == KeyEvent.VK_DOWN)
					DOWN = true;
				// H key to toggle heuristic
				if (key == KeyEvent.VK_H)
					tipoHeuristica = (tipoHeuristica + 1) % 2;
				// Space key to reset game
				if (key == KeyEvent.VK_SPACE)
					resetJogo();

			}

			public void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				// Arrow keys
				if (key == KeyEvent.VK_LEFT)
					LEFT = false;
				if (key == KeyEvent.VK_RIGHT)
					RIGHT = false;
				if (key == KeyEvent.VK_UP)
					UP = false;
				if (key == KeyEvent.VK_DOWN)
					DOWN = false;
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				mousex = e.getX();
				mousey = e.getY();
			}

			public void mouseDragged(MouseEvent e) {
				if (e.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK) {
					int mx = (int) ((e.getX() + mapa.MapX) / zoom) / 16;
					int my = (int) ((e.getY() + mapa.MapY) / zoom) / 16;
					if (mx < mapa.Altura && my < mapa.Largura)
						mapa.mapa[my][mx] = 1;
				}
			}
		});

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int mx = (int) ((e.getX() + mapa.MapX) / zoom) / 16;
				int my = (int) ((e.getY() + mapa.MapY) / zoom) / 16;

				if (mx >= mapa.Altura || my >= mapa.Largura)
					return;

				if (SwingUtilities.isRightMouseButton(e)) {
					mapa.mapa[my][mx] = mapa.mapa[my][mx] == 0 ? 1 : 0;
				} else if (SwingUtilities.isLeftMouseButton(e) && mapa.mapa[my][mx] == 0) {
					caminho = null;
					caminhoIndex = 0;
					synchronized (nodosPercorridos) {
						nodosPercorridos.clear();
					}
					long ini = System.currentTimeMillis();
					rodaBuscaAStar((int) (meuHeroi.X / 16), (int) (meuHeroi.Y / 16), mx, my);
					System.out.println("Tempo Final: " + (System.currentTimeMillis() - ini));
				}
			}
		});

		addMouseWheelListener(e -> {
			zoom *= e.getWheelRotation() > 0 ? 1.1f : 0.9f;
			ntileW = Math.min(1000, (int) ((960 / zoom) / 16) + 1);
			ntileH = Math.min(1000, (int) ((800 / zoom) / 16) + 1);
			mapa.NumeroTilesX = ntileW;
			mapa.NumeroTilesY = ntileH;
		});
	}

	private void initializeGame() {
		meuHeroi = new MeuAgente(10, 10, Color.red);
		listadeagentes.add(meuHeroi);
		mapa = new Mapa_Grid(100, 100, ntileW, ntileH);
		mapa.loadmapfromimage("/imagemlabirinto1000.png");
	}

	public boolean rodaBuscaAStar(int iniX, int iniY, int objX, int objY) {
		PriorityQueue<NodoAStar> openList = new PriorityQueue<>();
		HashSet<NodoAStar> closedSet = new HashSet<>();

		NodoAStar start = new NodoAStar(iniX, iniY, 0, heuristica(iniX, iniY, objX, objY), null);
		openList.add(start);

		// Decide movement directions based on heuristic
		int[][] directions;
		if (tipoHeuristica == 1) { // Euclidean → allow diagonal
			directions = new int[][] {
					{ 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 },
					{ 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }
			};
		} else { // Manhattan → only straight moves
			directions = new int[][] {
					{ 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 }
			};
		}

		while (!openList.isEmpty()) {
			NodoAStar current = openList.poll();

			System.out.println("Acessando nó: (" + current.x + ", " + current.y +
					") | Custo de Movimento: " + current.momevent_cost +
					", Custo da Heuristica: " + heuristica(current.x, current.y, objX, objY) +
					", Custo Total: " + (current.momevent_cost + heuristica(current.x, current.y, objX, objY)));

			if (current.x == objX && current.y == objY) {
				LinkedList<NodoAStar> path = new LinkedList<>();
				while (current != null) {
					path.addFirst(current);
					current = current.parent;
				}
				caminho = new int[path.size() * 2];
				int i = 0;
				for (NodoAStar n : path) {
					caminho[i++] = n.x;
					caminho[i++] = n.y;
					synchronized (nodosPercorridos) {
						nodosPercorridos.add(n.x + n.y * 1000);
					}
				}
				return true;
			}

			closedSet.add(current);
			synchronized (nodosPercorridos) {
				nodosPercorridos.add(current.x + current.y * 1000);
			}

			for (int[] dir : directions) {
				int nx = current.x + dir[0];
				int ny = current.y + dir[1];

				if (nx < 0 || ny < 0 || nx >= mapa.Largura || ny >= mapa.Altura)
					continue;
				if (mapa.mapa[ny][nx] != 0)
					continue;

				// Prevent diagonal cutting through corners
				if (dir[0] != 0 && dir[1] != 0) {
					if (mapa.mapa[current.y][current.x + dir[0]] != 0 ||
							mapa.mapa[current.y + dir[1]][current.x] != 0)
						continue;
				}

				// Cost: 10 (straight) or 14 (diagonal)
				int moveCost = (dir[0] == 0 || dir[1] == 0) ? 10 : 14;

				NodoAStar neighbor = new NodoAStar(
						nx,
						ny,
						current.momevent_cost + moveCost,
						heuristica(nx, ny, objX, objY),
						current);

				if (closedSet.contains(neighbor))
					continue;

				boolean inOpen = openList.stream()
						.anyMatch(n -> n.equals(neighbor) && n.momevent_cost <= neighbor.momevent_cost);
				if (!inOpen)
					openList.add(neighbor);
			}
		}

		return false;
	}

	private int heuristica(int x1, int y1, int x2, int y2) {
		if (tipoHeuristica == 1) {
			double dx = x1 - x2;
			double dy = y1 - y2;
			return (int) (Math.sqrt(dx * dx + dy * dy) * 10); // Euclidiana
		} else {
			return (Math.abs(x1 - x2) + Math.abs(y1 - y2)) * 10; // Manhattan
		}
	}

	public void startGame() {
		if (animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	}

	public void stopGame() {
		running = false;
	}

	public void run() {
		running = true;
		long diffTime = 0, previousTime = System.currentTimeMillis();
		int lastSecond = 0;

		createBufferStrategy(2);
		BufferStrategy strategy = getBufferStrategy();

		while (running) {
			gameUpdate(diffTime);
			Graphics g = strategy.getDrawGraphics();
			synchronized (this) {
				gameRender((Graphics2D) g);
			}
			strategy.show();

			try {
				Thread.sleep(100); // Movement delay for character
			} catch (InterruptedException ignored) {
			}

			long now = System.currentTimeMillis();
			diffTime = now - previousTime;
			previousTime = now;

			if (lastSecond != (int) (now / 1000)) {
				FPS = SFPS;
				SFPS = 1;
				lastSecond = (int) (now / 1000);
			} else
				SFPS++;
		}
		System.exit(0);
	}

	private void gameUpdate(long diffTime) {
		if (LEFT)
			posx -= 1000 * diffTime / 1000.0;
		if (RIGHT)
			posx += 1000 * diffTime / 1000.0;
		if (UP)
			posy -= 1000 * diffTime / 1000.0;
		if (DOWN)
			posy += 1000 * diffTime / 1000.0;

		if (caminho != null && caminhoIndex < caminho.length) {
			int nx = caminho[caminhoIndex];
			int ny = caminho[caminhoIndex + 1];
			meuHeroi.X = nx * 16 + 8;
			meuHeroi.Y = ny * 16 + 8;
			caminhoIndex += 2;
		}

		posx = Math.max(0, Math.min(posx, mapa.Largura * 16));
		posy = Math.max(0, Math.min(posy, mapa.Altura * 16));

		mapa.Posiciona((int) posx, (int) posy);
		listadeagentes.forEach(a -> a.SimulaSe((int) diffTime));
	}

	private void gameRender(Graphics2D dbg) {
		dbg.setColor(Color.white);
		dbg.fillRect(0, 0, PWIDTH, PHEIGHT);

		AffineTransform trans = dbg.getTransform();
		dbg.scale(zoom, zoom);

		try {
			mapa.DesenhaSe(dbg);
		} catch (Exception e) {
			System.out.println("Erro ao desenhar mapa");
		}

		synchronized (nodosPercorridos) {
			for (Integer nxy : nodosPercorridos) {
				int px = nxy % 1000;
				int py = nxy / 1000;
				dbg.setColor(Color.GREEN);
				dbg.fillRect(px * 16 - mapa.MapX, py * 16 - mapa.MapY, 16, 16);
			}
		}

		if (caminho != null) {
			for (int i = 0; i < caminho.length / 2; i++) {
				int nx = caminho[i * 2], ny = caminho[i * 2 + 1];
				dbg.setColor(Color.BLUE);
				dbg.fillRect(nx * 16 - mapa.MapX, ny * 16 - mapa.MapY, 16, 16);
			}
		}
		listadeagentes.forEach(a -> a.DesenhaSe(dbg, mapa.MapX, mapa.MapY));

		dbg.setTransform(trans);
		dbg.setFont(f);
		dbg.setColor(Color.BLUE);
		dbg.drawString("FPS: " + FPS, 10, 30);
		dbg.drawString("N: " + nodosPercorridos.size(), 100, 30);
		dbg.drawString("Heurística: " + (tipoHeuristica == 0 ? "Manhattan" : "Euclidiana"), 200, 30);
	}
} // Fim da classe GamePanel
