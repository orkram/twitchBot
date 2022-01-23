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
import {FilteredTermService} from "./services/FilteredTermService";
import {SelectedTermsService} from "./services/SelectedUserService";
import {WhiteListService} from "./services/WhiteListService";
import {MatButtonModule} from "@angular/material/button";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {FilterComponentComponent} from './twitch-app/filter-component/filter-component.component';
import {WhitelistComponentComponent} from './twitch-app/whitelist-component/whitelist-component.component';
import {BettingComponentComponent} from './twitch-app/betting-component/betting-component.component';
import {NotificationsComponentComponent} from './twitch-app/notifications-component/notifications-component.component';
import {SelectedWhitelistService} from "./services/SelectedWhitelistService";
import {NotificationService} from "./services/NotificationService";
import {SelectedNotificationService} from "./services/SelectedNotificationService";
import { UsercommandComponent } from './twitch-app/usercommand/usercommand.component';
import {UserCommandService} from "./services/UserCommandService";
import {SelectedCommandsService} from "./services/SelectedCommandsService";

@NgModule({
  declarations: [
    AppComponent,
    TwitchChatComponent,
    TwitchAppComponent,
    FilterComponentComponent,
    WhitelistComponentComponent,
    BettingComponentComponent,
    NotificationsComponentComponent,
    UsercommandComponent
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
    UserCommandService,
    FilteredTermService,
    WhiteListService,
    SelectedWhitelistService,
    SelectedTermsService,
    NotificationService,
    SelectedNotificationService,
    SelectedCommandsService
  ],
})
export class AppModule { }
