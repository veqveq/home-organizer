import { Component, OnInit } from '@angular/core';
import {Observable, tap} from "rxjs";
import {Dictionary} from "../../models/dictionary";
import {DictionaryService} from "../../services/dictionary.service";
import {ModalService} from "../../services/modal.service";

@Component({
  selector: 'app-dictionary-register',
  templateUrl: './dictionary-register.component.html'
})
export class DictionaryRegisterComponent implements OnInit {

  title = 'Справочники';
  dictionaries$: Observable<Dictionary[]>
  loading = false
  dictionaryFilter = ''

  constructor(
    private dictionaryService: DictionaryService,
    public modalService: ModalService
  ) {
  }

  ngOnInit(): void {
    this.loading = true
    this.dictionaries$ = this.dictionaryService.getAll().pipe(
      tap(() => this.loading = false)
    )
  }

}
