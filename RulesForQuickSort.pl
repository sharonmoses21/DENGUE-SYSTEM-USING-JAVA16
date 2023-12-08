%Rule
%Quick Sort

quicksort([H|T],SortedResult) :-
  divide(H,T,Larger,Smaller),
  quicksort(Larger,SortedLarger),
  quicksort(Smaller,SortedSmaller),
  append(SortedLarger,[H|SortedSmaller],SortedResult).

quicksort([],[]).


divide((PKey,PValue),[(CKey,CValue)|T],[(CKey,CValue)|Larger],Smaller) :-
    CValue >= PValue, divide((PKey,PValue),T,Larger,Smaller).
divide((PKey,PValue),[(CKey,CValue)|T],Larger,[(CKey,CValue)|Smaller]) :-
    CValue < PValue, divide((PKey,PValue),T,Larger,Smaller).

divide(_,[],[],[]).

