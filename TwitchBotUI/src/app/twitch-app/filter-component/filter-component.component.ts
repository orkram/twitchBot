import {AfterViewInit, Component, OnInit} from '@angular/core';
import {SelectedTermsService} from "../../services/SelectedUserService";
import {FilteredTermService} from "../../services/FilteredTermService";
import {FilteredTerm} from "../../model/FilteredTerm";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-filter-component',
  templateUrl: './filter-component.component.html',
  styleUrls: ['./filter-component.component.scss']
})
export class FilterComponentComponent implements OnInit, AfterViewInit{

  constructor( private filteredTermService: FilteredTermService, private selectedFiltersService: SelectedTermsService) { }

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
        this.terms = terms.map((x: FilteredTerm) => x)
        this.selectedFilters = {}
      }
    )

  }

  ngOnInit(): void {
  }

  ngAfterViewInit():void{
    this.refreshFilters()
  }

  submitTerm(formDirective: any): void{
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
          formDirective.resetForm();
        }
      )
  }

  removeFilters(): void {
    this.selectedFiltersService
      .getSelectedTerms()
      .map( (filter: any) => {
          let term = this.terms.find((t) => t.term == filter)
          this.filteredTermService.removeTerm(term!.id, term!.term).subscribe(
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

  onSelect(selected: any, term: any): void{
    this.selectedFilters[term.term] = selected;
    this.selectedFiltersService.setSelectedTerms(this.getSelectedFilters()) ;
  }
}
