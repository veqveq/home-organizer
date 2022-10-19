import {Component, Input, OnInit, Output} from '@angular/core';
import {DictionaryField} from "../../models/dictionary-field";
import {Dictionary} from "../../models/dictionary";
import {DictionaryItem} from "../../models/dictionary-item";
import {ItemService} from "../../services/item.service";
import {TSMap} from "typescript-map";
import {concatMap, delay} from "rxjs";

@Component({
  selector: 'app-dictionary-item-editor',
  templateUrl: './dictionary-item-editor.component.html'
})
export class DictionaryItemEditorComponent implements OnInit {
  @Input() fields: DictionaryField[]
  @Input() dictionary: Dictionary

  item: DictionaryItem = new class implements DictionaryItem {
    fieldValues: TSMap<string, any> = new TSMap();
    dictionaryId: string;
    id: string;
  };

  constructor(private itemService: ItemService) {
  }

  ngOnInit(): void {
    this.item.dictionaryId = this.dictionary.id
  }

  save() {
    if (this.dictionary.id) {
      const ID = this.dictionary.id
      this.itemService.create(ID, this.item)
        .pipe(
          // delay(500),
          concatMap(() => this.itemService.getAll(ID)))
        .subscribe()
    }
  }
}
