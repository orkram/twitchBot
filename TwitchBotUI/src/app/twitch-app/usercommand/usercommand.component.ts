import {AfterViewInit, Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {UserCommandService} from "../../services/UserCommandService";
import {UserCommand} from "../../model/UserCommand";
import {SelectedCommandsService} from "../../services/SelectedCommandsService";

@Component({
  selector: 'app-usercommand',
  templateUrl: './usercommand.component.html',
  styleUrls: ['./usercommand.component.scss']
})
export class UsercommandComponent implements OnInit, AfterViewInit {

  constructor( private userCommandService: UserCommandService, private selectedCommandsService: SelectedCommandsService) { }

  userCommands: UserCommand[] = []
  selectedCommands: {[key: string]: boolean} = {};

  commandsFrom: FormGroup = new FormGroup({
    signature: new FormControl('', Validators.compose([Validators.required])),
    output: new FormControl('', Validators.compose([Validators.required]))
  });


  getSelectedCommands(): Array<string>{
    return Object.keys(this.selectedCommands).filter((name: string) => this.selectedCommands[name]);
  }

  refreshCommands() {
    this.userCommandService.getCommands().subscribe(
      (terms: UserCommand[]) =>{
        console.log(terms)
        this.userCommands = terms.map((x: UserCommand) => x)
        this.selectedCommands = {}
      }
    )

  }

  ngOnInit(): void {
  }

  ngAfterViewInit():void{
    this.refreshCommands()
  }

  submitCommand(formDirective: any): void{
    this.userCommandService
      .addCommand(
        1,
        this.commandsFrom.value.signature,
        this.commandsFrom.value.output
      )
      .subscribe(
        _ => {},
        _ => {},
        () => {
          this.refreshCommands()
          formDirective.resetForm();
        }
      )
  }

  removeCommands(): void {
    this.selectedCommandsService
      .getSelectedCommands()
      .map( (filter: any) => {
          console.log(filter)
          let cmd = this.userCommands.find((t) => t.signature == filter)

          this.userCommandService.removeCommands(cmd!.id, cmd!.signature, cmd!.output).subscribe(
            _ => {
            },
            _ => {
            },
            () => {
              window.location.reload(); //ugly, should be unnecessary
            }
          )
        }
      );
  }

  unselect(): void{
    this.selectedCommandsService.clear();
    this.selectedCommands = {};
  }

  onSelect(selected: any, term: any): void{
    this.selectedCommands[term.signature] = selected;
    this.selectedCommandsService.setSelectedCommands(this.getSelectedCommands()) ;
  }

}
