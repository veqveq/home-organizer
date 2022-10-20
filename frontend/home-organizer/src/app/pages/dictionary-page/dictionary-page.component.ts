import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params} from "@angular/router";
import {DictionaryService} from "../../services/dictionary.service";
import {Dictionary} from "../../models/dictionary";
import {ItemService} from "../../services/item.service";
import {DictionaryItem} from "../../models/dictionary-item";
import {DictionaryField} from "../../models/dictionary-field";

@Component({
  selector: 'app-dictionary-page',
  templateUrl: './dictionary-page.component.html',
  styleUrls:['./dictionary-page.component.css']
})
export class DictionaryPageComponent implements OnInit {
  dictionary: Dictionary
  items: DictionaryItem[]

  constructor(
    private route: ActivatedRoute,
    private dictionaryService: DictionaryService,
    private itemService: ItemService
  ) {
  }

  ngOnInit(): void {
    this.route.params.subscribe((params: Params) => {
      this.dictionaryService.getById(params.id).subscribe(dic => {
        this.dictionary = dic
        this.itemService.getAll(params.id).subscribe(items => this.items = items)
      })
    })
  }

  public getValue(fld: DictionaryField, item: DictionaryItem): any {
    if (fld.id) {
      return JSON.parse(JSON.stringify(item.fieldValues))[fld.id]
    }
  }

  delete(itemId: string) {
    if (this.dictionary.id) {
      const ID = this.dictionary.id
      this.itemService.delete(ID, itemId)
        .subscribe(() => this.itemService.getAll(ID).subscribe(items => this.items = items))
    }
  }
}
