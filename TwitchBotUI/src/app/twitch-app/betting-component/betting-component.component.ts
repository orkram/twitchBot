import { Component, OnInit } from '@angular/core';
import {BettingService} from "../../services/BettingService";
import {BettingSession} from "../../model/BettingSession";

@Component({
  selector: 'app-betting-component',
  templateUrl: './betting-component.component.html',
  styleUrls: ['./betting-component.component.scss']
})
export class BettingComponentComponent implements OnInit {

  constructor(private bettingService: BettingService) { }



  ngOnInit(): void {
    this.betOngoing = false;
    this.updateBetState()
  }

  betOngoing = {}
  ongoingBet = {}


  finishBet(outcome: String){
    this.bettingService.finishBet(outcome).subscribe(
      next => {
        this.updateBetState()
      },
      error => {
        //dispay error message (no bets ongoing)
      }
    )
  }

  startBet(){
    // try to start bet
   this.bettingService.startBet().subscribe(
      value => {
        this.updateBetState()
      }
   )
  }

  updateBetState(){

    this.bettingService.getOngoingBet().subscribe(
      next =>{
        this.betOngoing =  next != null
        this.ongoingBet = next
      }
    )
  }
}
