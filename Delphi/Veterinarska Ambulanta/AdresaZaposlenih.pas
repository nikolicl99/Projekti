unit AdresaZaposlenih;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.ListBox,
  FMX.Controls.Presentation, FMX.Edit, FMX.StdCtrls, FireDAC.Stan.Intf,
  FireDAC.Stan.Option, FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS,
  FireDAC.Phys.Intf, FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt,
  Data.DB, FireDAC.Comp.DataSet, FireDAC.Comp.Client, FMX.Objects;

type
  TfrmAdresaZaposlenih = class(TForm)
    Opstina: TComboBox;
    Mesto: TComboBox;
    Adresa: TEdit;
    Broj: TEdit;
    Dodaj: TButton;
    Opstina_Label: TLabel;
    Mesto_Label: TLabel;
    Adresa_Label: TLabel;
    Broj_Label: TLabel;
    Nazad: TButton;
    StyleBook: TStyleBook;
    Navbar: TImage;
    Forma: TPanel;
   procedure Opstinabox;
    procedure MestoSelect(Sender: TObject);
    procedure OpstinaSelect(Sender: TObject);
   procedure DodajClick(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure NazadClick(Sender: TObject);
  private
    { Private declarations }
  SelectedMestoID: Integer;
  public
    { Public declarations }
  end;

var
  frmAdresaZaposlenih: TfrmAdresaZaposlenih;

implementation

{$R *.fmx}
uses Main, NoviZaposleni, SpisakZaposlenih;

procedure TfrmAdresaZaposlenih.DodajClick(Sender: TObject);
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

    MyQuery.SQL.Text := 'INSERT INTO Zaposleni (Ime, Prezime, User, Lozinka, Telefon, Email, Adresa_Stanovanja, Funkcija) ' +
                    'VALUES (:Ime, :Prezime, :User, :Lozinka, :Telefon, :Email, :AdresaID, :Funkcija)';
    MyQuery.ParamByName('Ime').AsString := frmNoviZaposleni.ZaposleniIme;
    MyQuery.ParamByName('Prezime').AsString := frmNoviZaposleni.ZaposleniPrezime;
    MyQuery.ParamByName('User').AsString := frmNoviZaposleni.ZaposleniUser;
    MyQuery.ParamByName('Lozinka').AsString := frmNoviZaposleni.ZaposleniLozinka;
    MyQuery.ParamByName('Telefon').AsString := frmNoviZaposleni.ZaposleniTelefon;
    MyQuery.ParamByName('Email').AsString := frmNoviZaposleni.ZaposleniEmail;
    MyQuery.ParamByName('AdresaID').AsInteger := AdresaID;
    MyQuery.ParamByName('Funkcija').AsInteger := frmNoviZaposleni.ZaposleniTipID;
    MyQuery.ExecSQL;
  finally
      MyQuery.Free;
      ShowMessage('Uspesno Registrovanje.');
    frmZaposleni.show;
    self.hide;
  end;
end;

procedure TfrmAdresaZaposlenih.FormCreate(Sender: TObject);
begin
  Opstinabox;

  if Opstina.Items.Count > 0 then
  begin
    Opstina.ItemIndex := 0;
    OpstinaSelect(Opstina);
  end;
end;

procedure TfrmAdresaZaposlenih.Opstinabox;
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

  procedure TfrmAdresaZaposlenih.OpstinaSelect(Sender: TObject);
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

procedure TfrmAdresaZaposlenih.MestoSelect(Sender: TObject);
begin
  if Mesto.ItemIndex <> -1 then
  begin
    SelectedMestoID := Integer(Mesto.Items.Objects[Mesto.ItemIndex]);
  end;
end;

procedure TfrmAdresaZaposlenih.NazadClick(Sender: TObject);
begin
frmnovizaposleni.show;
self.hide;
end;

end.
