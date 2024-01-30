unit UplataPlata;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.StdCtrls,
  FMX.Controls.Presentation, FMX.Edit, FMX.ListBox, FMX.DateTimeCtrls,
  FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Param,
  FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf, FireDAC.DApt.Intf,
  FireDAC.Stan.Async, FireDAC.DApt, Data.DB, FireDAC.Comp.DataSet,
  FireDAC.Comp.Client, FMX.Objects;

type
  TfrmDodajUplatu = class(TForm)
    PlataSat: TEdit;
    BrojSat: TEdit;
    Valuta: TComboBox;
    PlataSat_Label: TLabel;
    BrojSat_Label: TLabel;
    Valuta_Label: TLabel;
    DatumOd: TDateEdit;
    DatumOd_Label: TLabel;
    Nazad: TButton;
    Uplata: TButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    DatumDo: TDateEdit;
    DatumDo_Label: TLabel;
    procedure FormCreate(Sender: TObject);
    procedure NazadClick(Sender: TObject);
    procedure DatumOdChange(Sender: TObject);
    procedure UplataClick(Sender: TObject);
    function GetIDZaposlenog(const imeZaposlenog: string): Integer;
    procedure DatumDoChange(Sender: TObject);
  private
    { Private declarations }
    SelectedDateOd: string;
    SelectedDateDo: string;
  public
    { Public declarations }
  end;

var
  frmDodajUplatu: TfrmDodajUplatu;

implementation

{$R *.fmx}
uses Main, SpisakZaposlenih, SpisakPlata;

procedure TfrmDodajUplatu.DatumOdChange(Sender: TObject);
begin
SelectedDateOd:= FormatDateTime('dd/mm/yyyy',DatumOd.Date);
end;

procedure TfrmDodajUplatu.DatumDoChange(Sender: TObject);
begin
SelectedDateDo:= FormatDateTime('dd/mm/yyyy',DatumDo.Date);
end;

procedure TfrmDodajUplatu.FormCreate(Sender: TObject);
//var
//  MyQuery: TFDQuery;
begin
//  MyQuery := TFDQuery.Create(nil);
//  try
//    MyQuery.Connection := GlobalConnection;
//    MyQuery.SQL.Clear;
//    MyQuery.SQL.Text := 'SELECT * FROM Zaposleni';
//    MyQuery.Open;
//
//    Zaposleni.Clear;
//
//    while not MyQuery.Eof do
//    begin
//      Zaposleni.Items.Add(MyQuery.FieldbyName('Ime').AsString + ' ' + MyQuery.FieldByName('Prezime').AsString);
//      MyQuery.Next;
//    end;
//  finally
//    MyQuery.Free;
//    MyQuery.Close;
//  end;
end;

procedure TfrmDodajUplatu.NazadClick(Sender: TObject);
begin
frmplate.show;
self.hide;
end;

procedure TfrmDodajUplatu.UplataClick(Sender: TObject);
var
Query: TFDQuery;
begin
Query:= TFDQuery.Create(nil);
try
    Query.Connection := GlobalConnection;
    Query.SQL.Text := 'INSERT INTO Plate_Zaposlenih (IDZaposlenog, Datum_Od, Datum_Do, Plata_Po_Satu, Broj_Radnih_Sati, Ukupno_Za_Placanje, Valuta, Aktivna) VALUES (:IDZaposlenog, :Datum_Od, :Datum_Do, :Plata_Po_Satu, :Broj_Radnih_Sati, :Ukupno_Za_Placanje, :Valuta, :Aktivna)';
    Query.ParamByName('IDZaposlenog').AsInteger := frmZaposleni.SelectedID;
    Query.ParamByName('Datum_Od').AsString := SelectedDateOd;
    Query.ParamByName('Datum_Do').AsString := SelectedDateDo;
    Query.ParamByName('Plata_Po_Satu').AsInteger := strtoint(PlataSat.text);
    Query.ParamByName('Broj_Radnih_Sati').AsInteger := strtoint(BrojSat.Text);
    Query.ParamByName('Ukupno_Za_Placanje').AsInteger := strtoint(PlataSat.text) * strtoint(BrojSat.Text);
    Query.ParamByName('Valuta').AsString := Valuta.Selected.Text;
    Query.ParamByName('Aktivna').AsString := '2';
    Query.ExecSQL;

  finally
    Query.Free;
    ShowMessage('Plata uspesno dodata.');
    frmPlate.show;
    self.hide;
  end;
end;

function TfrmDodajUplatu.GetIDZaposlenog(const imeZaposlenog: string): Integer;
var
  MyQuery: TFDQuery;
  Ime, Prezime: string;
begin
  Result := -1;
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT idzaposlenog, Ime, Prezime FROM zaposleni';
    MyQuery.Open;

    while not MyQuery.Eof do
    begin
      Ime := MyQuery.FieldByName('Ime').AsString;
      Prezime := MyQuery.FieldByName('Prezime').AsString;

      if SameText(imeZaposlenog, Ime + ' ' + Prezime) then
      begin
        Result := MyQuery.FieldByName('idzaposlenog').AsInteger;
      end;

      MyQuery.Next;
    end;
  finally
    MyQuery.Free;
  end;
end;
end.
