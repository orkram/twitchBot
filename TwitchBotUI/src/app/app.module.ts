import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {TwitchChatComponent} from './twitch-chat/twitch-chat.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {TwitchAppComponent} from './twitch-app/twitch-app.component';
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatIconModule} from "@angular/material/icon";
import {MatTableModule} from "@angular/material/table";
import {MatListModule} from "@angular/material/list";
import {HttpClientModule} from "@angular/common/http";
import {BettingService} from "./services/BettingService";
import {CustomCommandService} from "./services/CustomCommandService";
import {FilteredTermService} from "./services/FilteredTermService";
import {SelectedUsersService} from "./services/SelectedUserService";
import {WhiteListService} from "./services/WhiteListService";
import {MatButtonModule} from "@angular/material/button";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { FilterComponentComponent } from './twitch-app/filter-component/filter-component.component';
import { WhitelistComponentComponent } from './twitch-app/whitelist-component/whitelist-component.component';

@NgModule({
  declarations: [
    AppComponent,
    TwitchChatComponent,
    TwitchAppComponent,
    FilterComponentComponent,
    WhitelistComponentComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatIconModule,
    MatTableModule,
    MatListModule,
    HttpClientModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatFormFieldModule,
    ReactiveFormsModule,
  ],
  bootstrap: [AppComponent],
  providers: [
    BettingService,
    CustomCommandService,
    FilteredTermService,
    SelectedUsersService,
    WhiteListService
  ],
})
export class AppModule { }
