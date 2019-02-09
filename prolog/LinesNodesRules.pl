%Acceptable highway types
driveable(primary_link).
driveable(secondary_link).
driveable(tertiary_link).
driveable(primary).
driveable(secondary).
driveable(tertiary).
driveable(motorway).
driveable(motorway_link).
driveable(living_street).
driveable(unclassified).
driveable(trunk).
driveable(residential).

%Access field types
accessible(yes).
accessible(empty).
accessible(permissive).
accessible(allowed).
accessible(destination).

%Convert oneway values to the ones required in the main program
convertOneway(yes,1).
convertOneway(empty,0).
convertOneway(no,0).
convertOneway(oneway,1).
convertOneway(-1,-1).

convertLit(no,false).
convertLit(_,true).

%Default max speed values for different types of highways
convertMaxspeed(primary_link, empty, 90).
convertMaxspeed(secondary_link, empty, 60).
convertMaxspeed(tertiary_link, empty, 60).
convertMaxspeed(primary, empty, 90).
convertMaxspeed(secondary, empty, 60).
convertMaxspeed(tertiary, empty, 60).
convertMaxspeed(motorway, empty, 110).
convertMaxspeed(motorway_link, empty, 90).
convertMaxspeed(living_street, empty, 20).
convertMaxspeed(unclassified, empty, 60).
convertMaxspeed(trunk, empty, 90).
convertMaxspeed(residential, empty, 30).
convertMaxspeed(_Highway, Maxspeed, Maxspeed).

convertToll(yes, true).
convertToll(empty, false).
convertToll(no, false).

%Asserts a fact describing the line, if it respects certain criteria
addLine(Id, Highway, Oneway, Lit, Maxspeed, Access, Toll):-
	(driveable(Highway), convertOneway(Oneway, Direction), convertLit(Lit, IsLit),convertMaxspeed(Highway,Maxspeed,NewMaxspeed), convertToll(Toll, IsToll), accessible(Access) ->
		assert(line(Id,Direction,IsLit,NewMaxspeed,IsToll))
		; true).


%Predicate determining traffic level value 
traffic(ID, Time, TrafficLevel):-
	trafficInfo(ID, TimeList), member((Time,TrafficLevel),TimeList).

%Default value
traffic(_,_,unknown).