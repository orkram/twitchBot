import { Component, OnInit } from '@angular/core';
import {WhiteListService} from "../services/WhiteListService";
import {WhiteList} from "../model/WhiteList";
import {SelectedUsersService} from "../services/SelectedUserService";
import {FilteredTermService} from "../services/FilteredTermService";
import {FilteredTerm} from "../model/FilteredTerm";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-twitch-app',
  templateUrl: './twitch-app.component.html',
  styleUrls: ['./twitch-app.component.scss'],
  providers: [WhiteListService]
})
export class TwitchAppComponent implements OnInit {

  constructor( private filteredTermService: FilteredTermService, private whiteListService: WhiteListService, private selectedFiltersService: SelectedUsersService) { }

  terms: FilteredTerm[] = []
  selectedFilters: {[key: string]: boolean} = {};

  filterForm: FormGroup = new FormGroup({
    term: new FormControl('', Validators.compose([Validators.required])),
  });

  getSelectedFilters(): Array<string>{
    return Object.keys(this.selectedFilters).filter((name: string) => this.selectedFilters[name]);
  }

  refreshFilters() {
    this.filteredTermService.getTerms().subscribe(
      (terms: FilteredTerm[]) =>{
        console.log(terms)
        this.terms = terms.map((x: FilteredTerm) => x)
      }
    )

  }

  ngOnInit(): void {
    this.refreshFilters()
  }

  submitTerm(): void{
    console.log(this.filterForm.value.term)
      this.filteredTermService
        .addTerm(
          1,
          this.filterForm.value.term
        )
        .subscribe(
          _ => {},
          _ => {},
          () => {
            this.refreshFilters()
          }
        )
    this.filterForm.value.clear

  }

  removeFilters(): void {
    this.selectedFiltersService
      .getSelectedUsers()
      .map( (filter: any) => {

          let term = this.terms.find((t) => t.term == filter)
          return this.filteredTermService.removeTerm(term!.id, term!.term).subscribe(
            _ => {
            },
            _ => {
            },
            () => {
              this.refreshFilters()
            }
          )
        }
      );
  }

  unselect(): void{
    this.selectedFiltersService.clear();
    this.selectedFilters = {};
  }

  onSelect(filter: any): void{
    console.log(filter)
    this.selectedFilters[filter] = filter;
    this.selectedFiltersService.setSelectedUsers(this.terms.map( (x: FilteredTerm) => x.term)) ;
  }
}
