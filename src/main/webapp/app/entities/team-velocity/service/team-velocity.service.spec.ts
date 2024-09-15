import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ITeamVelocity } from '../team-velocity.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../team-velocity.test-samples';

import { TeamVelocityService } from './team-velocity.service';

const requireRestSample: ITeamVelocity = {
  ...sampleWithRequiredData,
};

describe('TeamVelocity Service', () => {
  let service: TeamVelocityService;
  let httpMock: HttpTestingController;
  let expectedResult: ITeamVelocity | ITeamVelocity[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TeamVelocityService);
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

    it('should create a TeamVelocity', () => {
      const teamVelocity = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(teamVelocity).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TeamVelocity', () => {
      const teamVelocity = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(teamVelocity).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TeamVelocity', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TeamVelocity', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TeamVelocity', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTeamVelocityToCollectionIfMissing', () => {
      it('should add a TeamVelocity to an empty array', () => {
        const teamVelocity: ITeamVelocity = sampleWithRequiredData;
        expectedResult = service.addTeamVelocityToCollectionIfMissing([], teamVelocity);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(teamVelocity);
      });

      it('should not add a TeamVelocity to an array that contains it', () => {
        const teamVelocity: ITeamVelocity = sampleWithRequiredData;
        const teamVelocityCollection: ITeamVelocity[] = [
          {
            ...teamVelocity,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTeamVelocityToCollectionIfMissing(teamVelocityCollection, teamVelocity);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TeamVelocity to an array that doesn't contain it", () => {
        const teamVelocity: ITeamVelocity = sampleWithRequiredData;
        const teamVelocityCollection: ITeamVelocity[] = [sampleWithPartialData];
        expectedResult = service.addTeamVelocityToCollectionIfMissing(teamVelocityCollection, teamVelocity);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(teamVelocity);
      });

      it('should add only unique TeamVelocity to an array', () => {
        const teamVelocityArray: ITeamVelocity[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const teamVelocityCollection: ITeamVelocity[] = [sampleWithRequiredData];
        expectedResult = service.addTeamVelocityToCollectionIfMissing(teamVelocityCollection, ...teamVelocityArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const teamVelocity: ITeamVelocity = sampleWithRequiredData;
        const teamVelocity2: ITeamVelocity = sampleWithPartialData;
        expectedResult = service.addTeamVelocityToCollectionIfMissing([], teamVelocity, teamVelocity2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(teamVelocity);
        expect(expectedResult).toContain(teamVelocity2);
      });

      it('should accept null and undefined values', () => {
        const teamVelocity: ITeamVelocity = sampleWithRequiredData;
        expectedResult = service.addTeamVelocityToCollectionIfMissing([], null, teamVelocity, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(teamVelocity);
      });

      it('should return initial array if no TeamVelocity is added', () => {
        const teamVelocityCollection: ITeamVelocity[] = [sampleWithRequiredData];
        expectedResult = service.addTeamVelocityToCollectionIfMissing(teamVelocityCollection, undefined, null);
        expect(expectedResult).toEqual(teamVelocityCollection);
      });
    });

    describe('compareTeamVelocity', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTeamVelocity(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTeamVelocity(entity1, entity2);
        const compareResult2 = service.compareTeamVelocity(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTeamVelocity(entity1, entity2);
        const compareResult2 = service.compareTeamVelocity(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTeamVelocity(entity1, entity2);
        const compareResult2 = service.compareTeamVelocity(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
