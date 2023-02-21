import {Injectable} from '@angular/core';
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ErrorService {
  error$ = new Subject<string>()

  handle(msg: string){
    this.error$.next(msg)
  }

  clear(){
    this.error$.next('')
  }
}
