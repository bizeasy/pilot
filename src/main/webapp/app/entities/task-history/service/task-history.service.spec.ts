import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ITaskHistory } from '../task-history.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../task-history.test-samples';

import { TaskHistoryService } from './task-history.service';

const requireRestSample: ITaskHistory = {
  ...sampleWithRequiredData,
};

describe('TaskHistory Service', () => {
  let service: TaskHistoryService;
  let httpMock: HttpTestingController;
  let expectedResult: ITaskHistory | ITaskHistory[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TaskHistoryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a TaskHistory', () => {
      const taskHistory = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(taskHistory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TaskHistory', () => {
      const taskHistory = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(taskHistory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TaskHistory', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TaskHistory', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TaskHistory', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTaskHistoryToCollectionIfMissing', () => {
      it('should add a TaskHistory to an empty array', () => {
        const taskHistory: ITaskHistory = sampleWithRequiredData;
        expectedResult = service.addTaskHistoryToCollectionIfMissing([], taskHistory);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(taskHistory);
      });

      it('should not add a TaskHistory to an array that contains it', () => {
        const taskHistory: ITaskHistory = sampleWithRequiredData;
        const taskHistoryCollection: ITaskHistory[] = [
          {
            ...taskHistory,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTaskHistoryToCollectionIfMissing(taskHistoryCollection, taskHistory);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TaskHistory to an array that doesn't contain it", () => {
        const taskHistory: ITaskHistory = sampleWithRequiredData;
        const taskHistoryCollection: ITaskHistory[] = [sampleWithPartialData];
        expectedResult = service.addTaskHistoryToCollectionIfMissing(taskHistoryCollection, taskHistory);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(taskHistory);
      });

      it('should add only unique TaskHistory to an array', () => {
        const taskHistoryArray: ITaskHistory[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const taskHistoryCollection: ITaskHistory[] = [sampleWithRequiredData];
        expectedResult = service.addTaskHistoryToCollectionIfMissing(taskHistoryCollection, ...taskHistoryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const taskHistory: ITaskHistory = sampleWithRequiredData;
        const taskHistory2: ITaskHistory = sampleWithPartialData;
        expectedResult = service.addTaskHistoryToCollectionIfMissing([], taskHistory, taskHistory2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(taskHistory);
        expect(expectedResult).toContain(taskHistory2);
      });

      it('should accept null and undefined values', () => {
        const taskHistory: ITaskHistory = sampleWithRequiredData;
        expectedResult = service.addTaskHistoryToCollectionIfMissing([], null, taskHistory, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(taskHistory);
      });

      it('should return initial array if no TaskHistory is added', () => {
        const taskHistoryCollection: ITaskHistory[] = [sampleWithRequiredData];
        expectedResult = service.addTaskHistoryToCollectionIfMissing(taskHistoryCollection, undefined, null);
        expect(expectedResult).toEqual(taskHistoryCollection);
      });
    });

    describe('compareTaskHistory', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTaskHistory(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTaskHistory(entity1, entity2);
        const compareResult2 = service.compareTaskHistory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTaskHistory(entity1, entity2);
        const compareResult2 = service.compareTaskHistory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTaskHistory(entity1, entity2);
        const compareResult2 = service.compareTaskHistory(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
