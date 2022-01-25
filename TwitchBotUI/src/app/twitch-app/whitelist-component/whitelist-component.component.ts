import {AfterViewInit, Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {WhiteListService} from "../../services/WhiteListService";
import {WhiteList} from "../../model/WhiteList";
import {SelectedWhitelistService} from "../../services/SelectedWhitelistService";

@Component({
  selector: 'app-whitelist-component',
  templateUrl: './whitelist-component.component.html',
  styleUrls: ['./whitelist-component.component.scss']
})
export class WhitelistComponentComponent implements OnInit, AfterViewInit{

  constructor( private whitelistedTermService: WhiteListService, private selectedWhitelistService: SelectedWhitelistService) { }

  whitelistedTerms: WhiteList[] = []
  selectedWhitelists: {[key: string]: boolean} = {};

  whitelistForm: FormGroup = new FormGroup({
    whitelist: new FormControl('', Validators.compose([Validators.required])),
  });

  getSelectedFilters(): Array<string>{
    return Object.keys(this.selectedWhitelists).filter((name: string) => this.selectedWhitelists[name]);
  }

  refreshWhitelists() {
    this.whitelistedTermService.getTerms().subscribe(
      (terms: WhiteList[]) =>{
        this.whitelistedTerms = terms.map((x: WhiteList) => x)
        this.selectedWhitelists = {}
      }
    )

  }

  ngOnInit(): void {
  }

  ngAfterViewInit():void{
    this.refreshWhitelists()
  }

  submitTerm(formDirective: any): void{
    this.whitelistedTermService
      .addTerm(
        this.whitelistForm.value.whitelist
      )
      .subscribe(
        _ => {},
        _ => {},
        () => {
          this.refreshWhitelists()
          formDirective.resetForm();
        }
      )
  }

  removeFilters(): void {
    this.selectedWhitelistService
      .getSelectedWhitelists()
      .map( (filter: any) => {
          let whitelist = this.whitelistedTerms.find((t) => t.allowedDomain == filter)
          this.whitelistedTermService.removeTerm(whitelist!).subscribe(
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
    this.selectedWhitelistService.clear();
    this.selectedWhitelists = {};
  }

  onSelect(selected: any, whitelist: any): void{
    this.selectedWhitelists[whitelist.allowedDomain] = selected;
    this.selectedWhitelistService.setSelectedWhitelists(this.getSelectedFilters()) ;
  }
}
