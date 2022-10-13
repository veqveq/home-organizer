import {Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse, HttpParams} from "@angular/common/http";
import {Dictionary} from "../models/dictionary";
import {catchError, delay, map, Observable, retry, throwError} from "rxjs";
import {Page} from "../models/page";
import {ErrorService} from "./error.service";

@Injectable({
  providedIn: 'root'
})
export class DictionaryService {
  ROOT_API: string = "http://localhost:8081/ho/api/v1/dictionaries"

  constructor(
    private http: HttpClient,
    private errorService: ErrorService
  ) {
  }

  getAll(): Observable<Dictionary[]> {
    return this.http.get<Page<Dictionary>>(this.ROOT_API, {
      params: new HttpParams({
        fromObject: {
          page: 0,
          size: 2
        }
      })
    }).pipe(
      map(response => response.content),
      delay(500),
      retry(2),
      catchError(this.handleError.bind(this))
    )
  }

  create(dictionary: Dictionary): Observable<Dictionary> {
    return this.http.post<Dictionary>(this.ROOT_API, dictionary)
      .pipe(
        catchError(this.handleError.bind(this))
      )
  }

  private handleError(error: HttpErrorResponse) {
    this.errorService.handle(error.error.message)
    return throwError(() => error.message)
  }
}
