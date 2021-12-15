import {AfterViewInit, Component, OnInit} from '@angular/core';
import {SelectedUsersService} from "../../services/SelectedUserService";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {WhiteListService} from "../../services/WhiteListService";
import {WhiteList} from "../../model/WhiteList";

@Component({
  selector: 'app-whitelist-component',
  templateUrl: './whitelist-component.component.html',
  styleUrls: ['./whitelist-component.component.scss']
})
export class WhitelistComponentComponent implements OnInit, AfterViewInit{

  constructor( private whitelistedTermService: WhiteListService, private selectedFiltersService: SelectedUsersService) { }

  whitelistedTerms: WhiteList[] = []
  selectedFilters: {[key: string]: boolean} = {};

  whitelistForm: FormGroup = new FormGroup({
    whitelist: new FormControl('', Validators.compose([Validators.required])),
  });

  getSelectedFilters(): Array<string>{
    return Object.keys(this.selectedFilters).filter((name: string) => this.selectedFilters[name]);
  }

  refreshWhitelists() {
    this.whitelistedTermService.getTerms().subscribe(
      (terms: WhiteList[]) =>{
        console.log(terms)
        this.whitelistedTerms = terms.map((x: WhiteList) => x)
        this.selectedFilters = {}
      }
    )

  }

  ngOnInit(): void {
  }

  ngAfterViewInit():void{
    this.refreshWhitelists()
  }

  submitTerm(formDirective: any): void{
    console.log(this.whitelistForm.value.whitelist)
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
    this.selectedFiltersService
      .getSelectedUsers()
      .map( (filter: any) => {
          console.log(filter)
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
    this.selectedFiltersService.clear();
    this.selectedFilters = {};
  }

  onSelect(selected: any, whitelist: any): void{
    this.selectedFilters[whitelist.allowedDomain] = selected;
    this.selectedFiltersService.setSelectedUsers(this.getSelectedFilters()) ;
    console.log(this.getSelectedFilters())
  }
}
