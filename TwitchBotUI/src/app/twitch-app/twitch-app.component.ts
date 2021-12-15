import {Component, OnInit} from '@angular/core';
import {WhiteListService} from "../services/WhiteListService";

@Component({
  selector: 'app-twitch-app',
  templateUrl: './twitch-app.component.html',
  styleUrls: ['./twitch-app.component.scss'],
  providers: [WhiteListService]
})
export class TwitchAppComponent implements OnInit{

  constructor() {
  }

  ngOnInit(): void {
  }

}
