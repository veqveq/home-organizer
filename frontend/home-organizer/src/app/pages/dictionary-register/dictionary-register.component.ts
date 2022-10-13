import {Component, OnInit} from '@angular/core';
import {DictionaryService} from "../../services/dictionary.service";
import {ModalService} from "../../services/modal.service";

@Component({
  selector: 'app-dictionary-register',
  templateUrl: './dictionary-register.component.html'
})
export class DictionaryRegisterComponent implements OnInit {

  title = 'Справочники';
  loading = false
  dictionaryFilter = ''

  constructor(
    public dictionaryService: DictionaryService,
    public modalService: ModalService
  ) {
  }

  ngOnInit(): void {
    this.loading = true
    this.dictionaryService.getAll().subscribe(() => this.loading = false)
  }

}
