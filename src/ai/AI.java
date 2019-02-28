package ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import exceptions.OccupiedCellException;
import exceptions.UnallowedMovementException;
import exceptions.WrongTurnException;
import model.game.Direction;
import model.game.Game;
import model.pieces.Piece;
import model.pieces.heroes.Medic;
import model.pieces.heroes.Speedster;
import model.pieces.heroes.Super;
import model.pieces.heroes.Tech;
import model.pieces.sidekicks.SideKickP1;
import model.pieces.sidekicks.SideKickP2;

public class AI 
{
	static Game game;
	public AI(Game game) {this.game=game;}
	//given a piece evaluate each move
	static int [] pieceMoves(Piece p)
	{
		int [] nums=new int [8];
		//  0   ,1 ,  2    , 3  ,  4  ,   5     , 6  ,   7
		//UPLEFT,UP,UPRIGHT,LEFT,RIGHT,DOWNLEFT,DOWN,DOWNRIGHT
		Arrays.fill(nums, 1);		
		if(game.getCellAt(getNewPos(Direction.UPLEFT, p).x, getNewPos(Direction.UPLEFT, p).y).getPiece()!=null 
		&& game.getCellAt(getNewPos(Direction.UPLEFT, p).x, getNewPos(Direction.UPLEFT, p).y).getPiece().getOwner()==p.getOwner())
			nums[0]=0;
		if(game.getCellAt(getNewPos(Direction.UP, p).x, getNewPos(Direction.UP, p).y).getPiece()!=null 
		&& game.getCellAt(getNewPos(Direction.UP, p).x, getNewPos(Direction.UP, p).y).getPiece().getOwner()==p.getOwner())
			nums[1]=0;
		if(game.getCellAt(getNewPos(Direction.UPRIGHT, p).x, getNewPos(Direction.UPRIGHT, p).y).getPiece()!=null 
		&& game.getCellAt(getNewPos(Direction.UPRIGHT, p).x, getNewPos(Direction.UPRIGHT, p).y).getPiece().getOwner()==p.getOwner())
			nums[2]=0;
		if(game.getCellAt(getNewPos(Direction.LEFT, p).x, getNewPos(Direction.LEFT, p).y).getPiece()!=null 
		&& game.getCellAt(getNewPos(Direction.LEFT, p).x, getNewPos(Direction.LEFT, p).y).getPiece().getOwner()==p.getOwner())
			nums[3]=0;
		if(game.getCellAt(getNewPos(Direction.RIGHT, p).x, getNewPos(Direction.RIGHT, p).y).getPiece()!=null 
		&& game.getCellAt(getNewPos(Direction.RIGHT, p).x, getNewPos(Direction.RIGHT, p).y).getPiece().getOwner()==p.getOwner())
			nums[4]=0;
		if(game.getCellAt(getNewPos(Direction.DOWNLEFT, p).x, getNewPos(Direction.DOWNLEFT, p).y).getPiece()!=null 
		&& game.getCellAt(getNewPos(Direction.DOWNLEFT, p).x, getNewPos(Direction.DOWNLEFT, p).y).getPiece().getOwner()==p.getOwner())
			nums[5]=0;
		if(game.getCellAt(getNewPos(Direction.DOWN, p).x, getNewPos(Direction.DOWN, p).y).getPiece()!=null 
		&& game.getCellAt(getNewPos(Direction.DOWN, p).x, getNewPos(Direction.DOWN, p).y).getPiece().getOwner()==p.getOwner())
			nums[6]=0;
		if(game.getCellAt(getNewPos(Direction.DOWNRIGHT, p).x, getNewPos(Direction.DOWNRIGHT, p).y).getPiece()!=null 
		&& game.getCellAt(getNewPos(Direction.DOWNRIGHT, p).x, getNewPos(Direction.DOWNRIGHT, p).y).getPiece().getOwner()==p.getOwner())
			nums[7]=0;
		
		if(p instanceof SideKickP1)							{nums[5]=0;nums[6]=0;nums[7]=0;}
		else if(p instanceof SideKickP2)					{nums[0]=0;nums[1]=0;nums[2]=0;}
		else if(p instanceof Medic || p instanceof Super)	{nums[0]=0;nums[2]=0;nums[5]=0;nums[7]=0;}
		else if(p instanceof Tech)							{nums[1]=0;nums[3]=0;nums[4]=0;nums[6]=0;}
		return nums;
	}
	static ArrayList<Trio> getAllValidMoves()
	{
		ArrayList<Trio> ans=new ArrayList<Trio>();
		for(int i=0;i<7;i++)
		{
			for(int j=0;j<6;j++)
			{
				if(game.getCellAt(i, j).getPiece()!=null && game.getCellAt(i, j).getPiece().getOwner()==game.getCurrentPlayer())
				{
					int [] tmp=pieceMoves(game.getCellAt(i, j).getPiece());
					tmp=filterAttack(tmp,game.getCellAt(i, j).getPiece());
					if(tmp[0]>0)	ans.add(new Trio(Direction.UPLEFT, tmp[0], game.getCellAt(i, j).getPiece()));
					if(tmp[1]>0)	ans.add(new Trio(Direction.UP, tmp[1], game.getCellAt(i, j).getPiece()));
					if(tmp[2]>0)	ans.add(new Trio(Direction.UPRIGHT, tmp[2], game.getCellAt(i, j).getPiece()));
					if(tmp[3]>0)	ans.add(new Trio(Direction.LEFT, tmp[3], game.getCellAt(i, j).getPiece()));
					if(tmp[4]>0)	ans.add(new Trio(Direction.RIGHT, tmp[4], game.getCellAt(i, j).getPiece()));
					if(tmp[5]>0)	ans.add(new Trio(Direction.DOWNLEFT, tmp[5], game.getCellAt(i, j).getPiece()));
					if(tmp[6]>0)	ans.add(new Trio(Direction.DOWN, tmp[6], game.getCellAt(i, j).getPiece()));
					if(tmp[7]>0)	ans.add(new Trio(Direction.DOWNRIGHT, tmp[7], game.getCellAt(i, j).getPiece()));
				}
			}
		}
		return ans;
	}

	static int[] filterAttack(int [] old,Piece p)
	{
		if(old[0]!=0 && game.getCellAt(getNewPos(Direction.UPLEFT, p).x, getNewPos(Direction.UPLEFT, p).y).getPiece()!=null &&game.getCellAt(getNewPos(Direction.UPLEFT, p).x, getNewPos(Direction.UPLEFT, p).y).getPiece().getOwner()!=game.getCurrentPlayer())
			old[0]=10;
		if(old[1]!=0 && game.getCellAt(getNewPos(Direction.UP, p).x, getNewPos(Direction.UP, p).y).getPiece()!=null && game.getCellAt(getNewPos(Direction.UP, p).x, getNewPos(Direction.UP, p).y).getPiece().getOwner()!=game.getCurrentPlayer())
			old[1]=10;
		if(old[2]!=0 && game.getCellAt(getNewPos(Direction.UPRIGHT, p).x, getNewPos(Direction.UPRIGHT, p).y).getPiece()!=null && game.getCellAt(getNewPos(Direction.UPRIGHT, p).x, getNewPos(Direction.UPRIGHT, p).y).getPiece().getOwner()!=game.getCurrentPlayer())
			old[2]=10;
		if(old[3]!=0 && game.getCellAt(getNewPos(Direction.LEFT, p).x, getNewPos(Direction.LEFT, p).y).getPiece()!=null && game.getCellAt(getNewPos(Direction.LEFT, p).x, getNewPos(Direction.LEFT, p).y).getPiece().getOwner()!=game.getCurrentPlayer())
			old[3]=10;
		if(old[4]!=0 && game.getCellAt(getNewPos(Direction.RIGHT, p).x, getNewPos(Direction.RIGHT, p).y).getPiece()!=null && game.getCellAt(getNewPos(Direction.RIGHT, p).x, getNewPos(Direction.RIGHT, p).y).getPiece().getOwner()!=game.getCurrentPlayer())
			old[4]=10;
		if(old[5]!=0 && game.getCellAt(getNewPos(Direction.DOWNLEFT, p).x, getNewPos(Direction.DOWNLEFT, p).y).getPiece()!=null && game.getCellAt(getNewPos(Direction.DOWNLEFT, p).x, getNewPos(Direction.DOWNLEFT, p).y).getPiece().getOwner()!=game.getCurrentPlayer())
			old[5]=10;
		if(old[6]!=0 && game.getCellAt(getNewPos(Direction.DOWN, p).x, getNewPos(Direction.DOWN, p).y).getPiece()!=null && game.getCellAt(getNewPos(Direction.DOWN, p).x, getNewPos(Direction.DOWN, p).y).getPiece().getOwner()!=game.getCurrentPlayer())
			old[6]=10;
		if(old[7]!=0 && game.getCellAt(getNewPos(Direction.DOWNRIGHT, p).x, getNewPos(Direction.DOWNRIGHT, p).y).getPiece()!=null && game.getCellAt(getNewPos(Direction.DOWNRIGHT, p).x, getNewPos(Direction.DOWNRIGHT, p).y).getPiece().getOwner()!=game.getCurrentPlayer())
			old[7]=10;
		return old;
	}

	
	public void play() throws UnallowedMovementException, OccupiedCellException, WrongTurnException
	{
		ArrayList <Trio> possible=getBestMoves(getAllValidMoves());
		Collections.shuffle(possible);
		possible.get(0).piece.move(possible.get(0).d);
	}
	
	public static ArrayList<Trio> getBestMoves(ArrayList<Trio> old)
	{
		if(old.size()<=1)
			return old;
		Collections.sort(old);
		ArrayList<Trio> newlist =new ArrayList<Trio>();
		for(int i=old.size()-1;i>=0;i--)
		{
			if(old.get(i).rating==old.get(old.size()-1).rating)
				newlist.add(old.get(i));
			else
				return newlist;
		}
		return newlist;
	}
	public static Point getNewPos(Direction r, Piece piece)
	{
		int fac=1;
		if(piece instanceof Speedster)
			fac=2;
		Point p;
		int i=piece.getPosI();
		int j =piece.getPosJ();
		if(r.equals(Direction.UP))
			p=new Point((i-fac+7)%7, j);
		else if(r.equals(Direction.UPRIGHT))
			p=new Point((i-fac+7)%7, (j+fac)%6);
		else if(r.equals(Direction.RIGHT))
			p=new Point(i, (j+fac)%6);
		else if(r.equals(Direction.DOWNRIGHT))
			p=new Point((i+fac)%7, (j+fac)%6);
		else if(r.equals(Direction.DOWN))
			p=new Point((i+fac)%7, j);
		else if(r.equals(Direction.DOWNLEFT))
			p=new Point((i+fac)%7, (j-fac+6)%6);
		else if(r.equals(Direction.LEFT))
			p=new Point(i, (j-fac+6)%6);
		else //if(r.equals(Direction.UPLEFT))
			p=new Point((i-fac+7)%7, (j-fac+6)%6);
		return p;
	}
}
class Trio implements Comparable<Trio>
{
	Direction d;
	int rating;
	Piece piece;
	public Trio(Direction d,int rating, Piece piece)
	{
		this.d=d;
		this.rating=rating;
		this.piece=piece;
	}
	public String toString() {return d+" "+rating+" "+piece+"/n";}
	public int compareTo(Trio o) {return rating-o.rating;}
}
