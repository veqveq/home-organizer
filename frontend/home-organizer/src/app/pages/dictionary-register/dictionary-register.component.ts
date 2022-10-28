import {Component, OnInit} from '@angular/core';
import {DictionaryService} from "../../services/dictionary.service";
import {Page} from "../../models/page";
import {map, tap} from "rxjs";

@Component({
  selector: 'app-dictionary-register',
  templateUrl: './dictionary-register.component.html'
})
export class DictionaryRegisterComponent implements OnInit {

  page: Page<any> = new class implements Page<any> {
    content: any[];
    first: boolean;
    last: boolean;
    number: number;
    size: number;
    totalPages: number;
  }

  title = 'Справочники';
  load = false
  dictionaryFilter = ''

  constructor(
    public dictionaryService: DictionaryService
  ) {
  }

  ngOnInit(): void {
    this.load = true
    this.getAll()
  }

  getAll() {
    this.dictionaryService.getAll(this.page.number, this.page.size)
      .pipe(
        tap((resp) => {
          this.page = resp
        }),
        map((resp) => resp.content))
      .subscribe(() => this.load = false)
  }
}
