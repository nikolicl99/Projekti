unit AdresaKorisnika;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs,
  FMX.Controls.Presentation, FMX.Edit, FMX.ListBox, FireDAC.Stan.Intf,
  FireDAC.Stan.Option, FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS,
  FireDAC.Phys.Intf, FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt,
  Data.DB, FireDAC.Comp.DataSet, FireDAC.Comp.Client, FMX.StdCtrls, FMX.Objects;

type
  TfrmAdresaKorisnika = class(TForm)
    Mesto: TComboBox;
    Adresa: TEdit;
    Broj: TEdit;
    Opstina: TComboBox;
    Dodaj: TButton;
    Opstina_Label: TLabel;
    Mesto_Label: TLabel;
    Adresa_Label: TLabel;
    Broj_Label: TLabel;
    Nazad: TButton;
    Forma: TPanel;
    Navbar: TImage;
    StyleBook: TStyleBook;
    procedure FormCreate(Sender: TObject);
    procedure Opstinabox;
    procedure MestoSelect(Sender: TObject);
    procedure OpstinaSelect(Sender: TObject);
    procedure DodajClick(Sender: TObject);
    procedure NazadClick(Sender: TObject);
  private
    { Private declarations }
  SelectedMestoID: Integer;
  function GenerateUniqueCode: string;
  public
    { Public declarations }
  end;

var
  frmAdresaKorisnika: TfrmAdresaKorisnika;

implementation

{$R *.fmx}
uses Main, LoginForma, Registracija;

procedure TfrmAdresaKorisnika.DodajClick(Sender: TObject);
var
  MyQuery: TFDQuery;
  AdresaID: Integer;
begin
  if Mesto.ItemIndex = -1 then
  begin
    ShowMessage('Molimo izaberite mesto.');
    Exit;
  end;

  var NazivMesta: string := Mesto.Selected.Text;
  var Ulica: string := Adresa.Text;
  var Broj_Edit: string := Broj.Text;

  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'INSERT INTO Adresa_Stanovanja (IDMesta, Adresa, Broj) ' +
                        'VALUES (:IDMesta, :Ulica, :Broj)';
    MyQuery.ParamByName('IDMesta').AsInteger := SelectedMestoID;
    MyQuery.ParamByName('Ulica').AsString := Ulica;
    MyQuery.ParamByName('Broj').AsString := Broj_Edit;
    MyQuery.ExecSQL;

    MyQuery.SQL.Text := 'SELECT LAST_INSERT_ROWID()';
    MyQuery.Open;
    AdresaID := MyQuery.Fields[0].AsInteger;
    MyQuery.Close;

    MyQuery.SQL.Text := 'INSERT INTO Vlasnici (ImeVlasnika, PrezimeVlasnika, Email, Sifra, Telefon, Adresa_Stanovanja, Sifra_Validacije) ' +
                    'VALUES (:Ime, :Prezime, :Email, :Sifra, :Telefon, :AdresaID, :Validacija)';
    MyQuery.ParamByName('Ime').AsString := frmSignUp.RegistracijaIme;
    MyQuery.ParamByName('Prezime').AsString := frmSignUp.RegistracijaPrezime;
    MyQuery.ParamByName('Email').AsString := frmSignUp.RegistracijaEmail;
    MyQuery.ParamByName('Sifra').AsString := frmSignUp.RegistracijaSifra;
    MyQuery.ParamByName('Telefon').AsString := frmSignUp.RegistracijaTelefon;
    MyQuery.ParamByName('AdresaID').AsInteger := AdresaID;
    MyQuery.ParamByName('Validacija').AsString := GenerateUniqueCode;
    MyQuery.ExecSQL;
  finally
    MyQuery.Free;
    ShowMessage('Uspesno Registrovanje.');
    frmLogIn.show;
    self.hide;
  end;
end;

procedure TfrmAdresaKorisnika.FormCreate(Sender: TObject);
begin
  Opstinabox;

  if Opstina.Items.Count > 0 then
  begin
    Opstina.ItemIndex := 0;
    OpstinaSelect(Opstina);
  end;
end;

procedure TfrmAdresaKorisnika.Opstinabox;
var
  MyQuery: TFDQuery;
begin
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    Opstina.Items.Clear;
    MyQuery.SQL.Text := 'SELECT * FROM OPSTINA';
    MyQuery.Open;
    while not MyQuery.Eof do
    begin
      Opstina.Items.AddObject(MyQuery.FieldByName('Naziv_Opstine').AsString, TObject(MyQuery.FieldByName('IDOpstine').AsInteger));
      MyQuery.Next;
    end;
    MyQuery.Close;
  finally
    MyQuery.Free;
  end;
end;

procedure TfrmAdresaKorisnika.OpstinaSelect(Sender: TObject);
var
  MyQuery: TFDQuery;
begin
  if Opstina.ItemIndex <> -1 then
  begin
    MyQuery := TFDQuery.Create(nil);
    try
      MyQuery.Connection := GlobalConnection;
      Mesto.Items.Clear;
      MyQuery.SQL.Text := 'SELECT * FROM MESTO WHERE IDOpstine = :IDOpstine';
      MyQuery.ParamByName('IDOpstine').AsInteger := Integer(Opstina.Items.Objects[Opstina.ItemIndex]);
      MyQuery.Open;
      while not MyQuery.Eof do
      begin
        Mesto.Items.AddObject(MyQuery.FieldByName('Naziv_Mesta').AsString, TObject(MyQuery.FieldByName('IDMesta').AsInteger));
        MyQuery.Next;
      end;
      MyQuery.Close;
    finally
      MyQuery.Free;
    end;
  end;
end;

procedure TfrmAdresaKorisnika.MestoSelect(Sender: TObject);
begin
  if Mesto.ItemIndex <> -1 then
  begin
    SelectedMestoID := Integer(Mesto.Items.Objects[Mesto.ItemIndex]);
  end;
end;

procedure TfrmAdresaKorisnika.NazadClick(Sender: TObject);
begin
frmsignup.show;
self.hide;
end;

function TfrmAdresaKorisnika.GenerateUniqueCode: string;
const
  Letters: array[0..25] of Char = ('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                                   'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z');
var
  Code: string;
  RandomNum: Integer;
  RandomLetter: Char;
begin
  Randomize;
  Code := '';
  for var i := 1 to 3 do
  begin
    RandomNum := Random(10);
    Code := Code + IntToStr(RandomNum);
  end;

  Randomize;
  RandomLetter := Letters[Random(26)];

  Result := Code + RandomLetter;
end;


end.


