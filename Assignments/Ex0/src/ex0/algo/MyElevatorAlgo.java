package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

import java.util.*;


public class MyElevatorAlgo implements ElevatorAlgo {
    public static final int REST = 0, UP = 1, DOWN = -1;
    private ArrayList<CallForElevator>[] Calls;
    private Building _building;
    private ArrayList<Elevator> ListOfElevator;
    private PriorityQueue<Integer>[] CallsFloor;

    public MyElevatorAlgo(Building b) {
        this._building = b;
        this.ListOfElevator = new ArrayList<Elevator>();
        for (int i = 0; i < ListOfElevator.size(); i++) {
            ListOfElevator.add(_building.getElevetor(i));
        }
        this.Calls = new ArrayList[_building.numberOfElevetors()];
        for (int i = 0; i < Calls.length; i++) {
            Calls[i] = new ArrayList<>();
        }
        this.CallsFloor = new PriorityQueue[_building.numberOfElevetors()];
        for (int i = 0; i < CallsFloor.length; i++) {
            CallsFloor[i] = new PriorityQueue<Integer>();
        }
    }

    @Override
    public Building getBuilding() {
        return _building;
    }

    @Override
    public String algoName() {
        return "Ex0_OOP_MyElevatorAlgo";
    }

    private double CalcluateTimeToArrive(Elevator el, int floor) {
        int diffBetweenFloors = Math.abs(el.getPos() - floor);
        HashSet<Integer> ArraysSet = new HashSet<>();
        if (Calls[el.getID()].isEmpty()) {
            return 0;
        }
        for (int i = 1; i < Calls[el.getID()].size(); i++) {
            if (Calls[el.getID()].get(i).getSrc() < floor)
                ArraysSet.add(Calls[el.getID()].get(i).getSrc());
            if (Calls[el.getID()].get(i).getDest() < floor)
                ArraysSet.add(Calls[el.getID()].get(i).getDest());
        }
        int numberOfStops = ArraysSet.size();
        double totalTimeToOpen = numberOfStops * el.getTimeForOpen(),
                totalTimeToClose = numberOfStops * el.getTimeForClose(),
                totalTimeToPassAllFloors = diffBetweenFloors * el.getSpeed(),
                totalTimeToArrive = totalTimeToOpen + totalTimeToClose + totalTimeToPassAllFloors;
        return totalTimeToArrive;
    }

    /**
     * send to the user number of the right elevator
     * @param c the call for elevator (src, dest)
     * @return the number of the best elevator
     */
    @Override
    public int allocateAnElevator(CallForElevator c)
    {
        int index = 0;
        if(_building.numberOfElevetors() == 0)
        {
            CallsFloor[index].add(c.getSrc());
            CallsFloor[index].add(c.getDest());
            return -1;
        }
        if (_building.numberOfElevetors() == 1) {
            CallsFloor[index].add(c.getSrc());
            CallsFloor[index].add(c.getDest());
            return 0;
        }
        double minTimeToArrive = Integer.MAX_VALUE, tmpTime;
        for (int i = 0; i < ListOfElevator.size(); i++) {
            if (ListOfElevator.get(i).getState() != c.getType())
                continue;
            if(ListOfElevator.get(i).getState() == -2)
                continue;
            if (c.getType() == 1) {
                if (ListOfElevator.get(i).getPos() > c.getSrc())
                    continue;
            }
            if (c.getType() == -1) {
                if (ListOfElevator.get(i).getPos() < c.getSrc())
                    continue;
            }
            tmpTime = CalcluateTimeToArrive(ListOfElevator.get(i), c.getSrc());
            if (tmpTime < minTimeToArrive) {
                minTimeToArrive = tmpTime;
                index = i;
            }
        }
        CallsFloor[index].add(c.getSrc());
        CallsFloor[index].add(c.getDest());
        return index;
    }

    @Override
    public void cmdElevator(int elev) { //send the elevator to the call
        Elevator currElevator = this.getBuilding().getElevetor(elev);
        if (!this.CallsFloor[elev].isEmpty()) {
            currElevator.goTo(this.CallsFloor[elev].poll());
        }
    }
}