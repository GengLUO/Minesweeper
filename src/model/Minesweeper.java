package model;

import java.util.ArrayList;


public class Minesweeper extends AbstractMineSweeper{
    private int h;
    private int w;
    private int flagCount = 0;
    AbstractTile[][] tiles;
    @Override
    public int getWidth() {
        return w;
    }

    @Override
    public int getHeight() {
        return h;
    }

    @Override
    public void startNewGame(Difficulty level) {
        if (level.equals(Difficulty.EASY))
        {
            this.startNewGame(5,5,1);
        }
        else if (level.equals(Difficulty.MEDIUM))
        {
            this.startNewGame(7,7,15);
        }
        else if (level.equals(Difficulty.HARD))
        {
            this.startNewGame(10,10,30);
        }
    }

    @Override
    public void startNewGame(int row, int col, int explosionCount) {
        this.h = row;
        this.w = col;
        this.tiles = new AbstractTile[h][w];
        ArrayList<Integer> indexhused = new ArrayList<>();
        ArrayList<Integer> indexwused = new ArrayList<>();
        int indexh,indexw;
        for (int i = 0;i<h;i++)
        {
            for (int m = 0;m<w;m++)
            {
                this.tiles[i][m]=this.generateEmptyTile();
            }
        }
        for (int i = 0; i< explosionCount; i++)
        {
            indexh = (int)(Math.random()*h);
            indexw = (int)(Math.random()*w);
            for (int m = 0;m<indexhused.size();m++)
            {
                if (indexh == indexhused.get(m)&&indexw == indexwused.get(m))
                {
                    indexh = (int)(Math.random()*h);
                    indexw = (int)(Math.random()*w);
                    m = 0;
                }
            }
            indexhused.add(indexh);
            indexwused.add(indexw);
            this.tiles[indexh][indexw]=this.generateExplosiveTile();
        }
        viewNotifier.notifyNewGame(h,w);
    }

    @Override
    public void toggleFlag(int x, int y) {
        if (tiles[x][y].isFlagged())
        {
            this.unflag(x,y);
        }
        else
        {
            this.flag(x,y);
        }
        viewNotifier.notifyFlagCountChanged(this.flagCount);
    }

    @Override
    public AbstractTile getTile(int x, int y) {
        if (x>=0&&x<w&&y>=0&&y<h) {
            return tiles[y][x];
        }
        else return null;
    }

    @Override
    public void setWorld(AbstractTile[][] world) {
        this.h = world.length;
        this.w = world[0].length;
        tiles = new AbstractTile[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                this.tiles[i][j]=world[i][j];
            }
        }
    }

    @Override
    public void open(int x, int y) {
        if (x>=0&&x<w&&y>=0&&y<h&&!tiles[x][y].isOpened())
        {
            boolean first = true;
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    if (tiles[i][j].isOpened())
                    {
                        first=false;
                    }
                }
            }
            if (first)
            {
                deactivateFirstTileRule(x,y);
            }
            if (!tiles[x][y].isExplosive())
            {
                tiles[x][y].open();
                tiles[x][y].unflag();
                int xmin = 1,xmax=1,ymin=1,ymax = 1;
                if (x==0) xmin = 0;
                if (x==w-1) xmax =0;
                if (y==0)ymin =0;
                if (y==h-1)ymax=0;
                int explosivecount = 0;
                for (int i = x-xmin;i<=x+xmax;i++)
                {
                    for (int m =y-ymin;m<=y+ymax;m++)
                    {
                        if (tiles[i][m].isExplosive())
                        {
                            explosivecount++;
                        }
                    }
                }
                if(explosivecount==0)
                {
                    for (int i = x-xmin;i<=x+xmax;i++)
                    {
                        for (int m =y-ymin;m<=y+ymax;m++)
                        {
                            this.open(m,i);
                        }
                    }
                }
                tiles[x][y].setNumber(explosivecount);
                viewNotifier.notifyOpened(x,y,explosivecount);
            }
            else
            {
                viewNotifier.notifyExploded(x,y);
                viewNotifier.notifyGameLost();
            }
        }
    }
    public void openSurround(int x, int y)
    {
        int xmin = 1,xmax=1,ymin=1,ymax = 1;
        if (x==0) xmin = 0;
        if (x==w-1) xmax =0;
        if (y==0)ymin =0;
        if (y==h-1)ymax=0;
        for (int i = x-xmin;i<=x+xmax;i++)
        {
            for (int m =y-ymin;m<=y+ymax;m++)
            {
                this.open(m,i);
            }
        }
    }

    @Override
    public void flag(int x, int y) {
        if ((x>=0&&x<w&&y>=0&&y<h)&&(!tiles[x][y].isOpened()))
        {
            tiles[x][y].flag();
            viewNotifier.notifyFlagged(x, y);
            this.flagCount++;
        }
    }

    @Override
    public void unflag(int x, int y) {
        tiles[x][y].unflag();
        viewNotifier.notifyUnflagged(x, y);
        this.flagCount--;
    }

    @Override
    public void deactivateFirstTileRule(int x,int y) {
        tiles[x][y] = this.generateEmptyTile();
    }

    @Override
    public AbstractTile generateEmptyTile() {
        AbstractTile tile = new AbstractTile() {
            boolean flaged = false;
            boolean opened = false;
            int numaround = 0;
            @Override
            public boolean open() {
                opened = true;
                return true;
            }

            @Override
            public void flag() {
                flaged = true;
            }

            @Override
            public void unflag() {
                flaged = false;
            }

            @Override
            public boolean isFlagged() {
                return flaged;
            }

            @Override
            public boolean isOpened() {
                return opened;
            }

            @Override
            public void setNumber(int num) {
                this.numaround=num;
            }

            @Override
            public int getNumber() {
                return this.numaround;
            }

            @Override
            public boolean isExplosive() {
                return false;
            }
        };
        return tile;
    }

    @Override
    public AbstractTile generateExplosiveTile() {
        AbstractTile tile = new AbstractTile() {
            boolean flaged = false;
            boolean opened = false;
            int numaround = -1;
            @Override
            public boolean open() {
                opened = true;
                return false;
            }

            @Override
            public void flag() {
                flaged = true;
            }

            @Override
            public void unflag() {
                flaged = false;
            }

            @Override
            public boolean isFlagged() {
                return flaged;
            }

            @Override
            public boolean isOpened() {
                return opened;
            }

            @Override
            public void setNumber(int num) {

            }

            @Override
            public int getNumber() {
                return -1;
            }

            @Override
            public boolean isExplosive() {
                return true;
            }
        };
        return tile;
    }


}
